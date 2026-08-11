package loggerForms.videocontrol.protocols.amp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.SwingUtilities;

import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.RecordState;
import loggerForms.videocontrol.StatusMessage;
import loggerForms.videocontrol.VideoControl;
import loggerForms.videocontrol.protocols.VideoProtocol;
import loggerForms.videocontrol.protocols.VideoProtocolProvider;

/**
 * AMP Protocol used a TCP IP link to the camera. 
 */
public class AMPProtocol extends VideoProtocol {
	
	private Socket tcpSocket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private DataOutputStream dataOutputStream;
	private Thread listenerThread;
	private Thread connectThread;
	private volatile boolean listen;

	private String chanOpenCmd = "CRAT0007204Vtr1\n";
	private String chanReaqCmd = "REAQ0007204Vtr1\n";
	private String chanCloseCmd = "STOP0000\n";
	
	private String startCmd = "CMDS00042002\n";
	private String stopCmd = "CMDS00042000\n";		

	private enum TCPAnswer  {ACK, NACK, ERROR, UNKNOWN, NOANS};
	private static final byte[] ack = {49, 48, 48, 49}; //1001
	private static final byte[] nack = {49, 49, 49, 49}; //1111
	private static final byte[] err = {50, 50, 50, 50}; //2222

	public AMPProtocol(VideoControl videoControl, VideoProtocolProvider protocolProvider, DeviceParameters deviceParameters) {
		super(videoControl, protocolProvider, deviceParameters);
	
	}

	@Override
	public String startRecording() {
		System.out.println("Call AMP Start recording");
		getVideoControl().notifyStateChange(this, new StatusMessage(RecordState.RECORDING, getDeviceParameters().recordLengthS));
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
			}
		});
		String ok = writeStringCommand(startCmd);
		return ok;
	}

	private String writeStringCommand(String command) {
		if (connectThread != null && connectThread.isAlive()) {
			System.out.println("Still starting connection on " + getDeviceParameters().ipAddress);
			return "Still connecting";
		}
		if (outputStream == null) {
			System.out.println("no output steram for " + getDeviceParameters().ipAddress);
			return "No output to device";
		}
		System.out.printf("Write command \"%s\" to %s\n", command, getDeviceParameters().ipAddress);
		try {
			outputStream.write(command.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return "IO Exception";
		}
		return null;
	}
	
	@Override
	public String stopRecording() {
		System.out.println("Call AMP Stop recording");
		if (dataOutputStream == null) {
			return "No output stream";
		}
		try {
			dataOutputStream.writeBytes(stopCmd);
		} catch (IOException e) {
			getVideoControl().notifyStateChange(this, new StatusMessage(RecordState.ERROR, e.getLocalizedMessage()));
			return "IO Exception";
		}
		getVideoControl().notifyStateChange(this, new StatusMessage(RecordState.IDLE, 20));
		return null;
	}

	@Override
	public String connect() {
		disconnect();
		getVideoControl().notifyStateChange(this, new StatusMessage(RecordState.CONNECTING, 0));
		connectThread = new Thread(new Runnable() {
			@Override
			public void run() {
				String err = openSocket();
				if (err != null) {
					getVideoControl().notifyStateChange(AMPProtocol.this, new StatusMessage(RecordState.ERROR, err));
					return;
				}
				else {
					err = writeStringCommand(chanOpenCmd);
					if (err == null) {
						getVideoControl().notifyStateChange(AMPProtocol.this, new StatusMessage(RecordState.IDLE, err));
					}
					else {
						getVideoControl().notifyStateChange(AMPProtocol.this, new StatusMessage(RecordState.ERROR, err));
					}
				}
			}
		});
		connectThread.start();
		return "Starting connect";
	}

	protected String openSocket() {
		AMPParameters ampParams = getDeviceParameters();
		try {
			tcpSocket = new Socket(ampParams.ipAddress, ampParams.port);
		} catch (IOException e) {
			getVideoControl().notifyStateChange(this, new StatusMessage(RecordState.ERROR, e.getLocalizedMessage()));
			return e.getMessage();
		}
		try {
			inputStream = tcpSocket.getInputStream();
		} catch (IOException e) {
			getVideoControl().notifyStateChange(this, new StatusMessage(RecordState.ERROR, e.getLocalizedMessage()));
			return e.getMessage();
		}
		try {
			outputStream = tcpSocket.getOutputStream();
			dataOutputStream = new DataOutputStream(outputStream);
		} catch (IOException e) {
			getVideoControl().notifyStateChange(this, new StatusMessage(RecordState.ERROR, e.getLocalizedMessage()));
			return e.getMessage();
		}
		
		// set up a TCP listener thread. 
		listenerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				listen();
			}
		});
		listenerThread.start();
		writeStringCommand(chanOpenCmd);
		getVideoControl().notifyStateChange(this, new StatusMessage(RecordState.IDLE, null));
		
		
		connectThread = null;
		return tcpSocket != null ? null : "No Socket open";
		
	}

	protected void listen() {
		System.out.println("Start TCP listening thread on " + getDeviceParameters().ipAddress);
		listen = true;
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		while (listen) {
			try {
				int available = inputStream.available();
				if (available < 1) {
					Thread.sleep(50);
					continue;
				}
				
				byte[] bytes = new byte[available];
				inputStream.read(bytes);
				//				String aLine = br.readLine();
				System.out.println("Line received: " + new String(bytes));
				TCPAnswer answer = interpretReturn(bytes);
				reportAnswer(answer);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("End TCP listening thread on " + getDeviceParameters().ipAddress);
	}
	
	private void reportAnswer(TCPAnswer answer) {
		if (answer == TCPAnswer.ERROR) {
			getVideoControl().notifyStateChange(this, new StatusMessage(RecordState.ERROR, "Error"));
		}
		else if (answer == TCPAnswer.ACK) {
			
		}
		else {
			getVideoControl().notifyStateChange(this, new StatusMessage(RecordState.ERROR, "Unknown Error"));
		}
	}

	private TCPAnswer interpretReturn(byte[] data) {
		if (data == null) {
			return TCPAnswer.NOANS;
		}
		else if (isSame(data, ack)) {
			return TCPAnswer.ACK;
		}
		else if (isSame(data, nack)) {
			return TCPAnswer.NACK;
		}
		else if (isSame(data, err)) {
			return TCPAnswer.ERROR;
		}
		System.out.printf("Unknown tcp return:");
		for (int i = 0; i < data.length; i++) {
			System.out.printf("%d", data[i]);
		}
		System.out.printf(" \"%s\"\n", new String(data));
		return TCPAnswer.UNKNOWN;
		
	}

	private boolean isSame(byte[] data, byte[] tst) {
		if (data == null) {
			return false;
		}
		if (data.length < tst.length) {
			return false;
		}
		for (int i = 0; i < tst.length; i++) {
			if (data[i] != tst[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String disconnect() {
		if (connectThread != null && connectThread.isAlive()) {
			connectThread.interrupt();
			try {
				connectThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			connectThread = null;
		}
		
		listen = false;
		boolean ok = true;
		if (tcpSocket != null) {
			try {
				tcpSocket.close();
			} catch (IOException e) {
				ok = false;
			}
		}
		tcpSocket = null;
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				ok = false;
			}
		}
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				ok = false;
			}
		}
		// that should have closed the listen thread, but wait half a sec to be sure
		if (listenerThread != null) {
			try {
				listenerThread.join(500);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
				ok = false;
			}
		}
		return ok ? null : "Error disconnecting";
	}

	@Override
	public AMPParameters getDeviceParameters() {
		DeviceParameters params = super.getDeviceParameters();
		if (params instanceof AMPParameters) {
			return (AMPParameters) params;
		}
		else {
			AMPParameters ap = new AMPParameters(getProtocolProvider().getName());
			return ap;
		}
	}

	@Override
	public void setDeviceParameters(DeviceParameters deviceParameters) {
		if (deviceParameters instanceof AMPParameters) {
			super.setDeviceParameters(deviceParameters);
		}
		else {
			super.setDeviceParameters(new AMPParameters(deviceParameters.name));
		}
	}


}
