package loggerForms.videocontrol.protocols.amp;

import java.awt.Window;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;


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

	public AMPProtocol(VideoControl videoControl, VideoProtocolProvider protocolProvider, DeviceParameters deviceParameters) {
		super(videoControl, protocolProvider, deviceParameters);
	
	}

	@Override
	public boolean startRecording() {
		boolean ok = writeStringCommand(startCmd);
		if (ok) {
			getVideoControl().notifyStateChange(this, new StatusMessage(RecordState.RECORDING, 20));
		}
		return ok;
	}

	private boolean writeStringCommand(String command) {
		if (connectThread != null && connectThread.isAlive()) {
			System.out.println("Still starting connection on " + getDeviceParameters().ipAddress);
			return false;
		}
		if (outputStream == null) {
			System.out.println("no output steram for " + getDeviceParameters().ipAddress);
			return false;
		}
		System.out.printf("Write command \"%s\" to %s", command, getDeviceParameters().ipAddress);
		try {
			outputStream.write(command.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public boolean stopRecording() {
		if (dataOutputStream == null) {
			return false;
		}
		try {
			dataOutputStream.writeBytes(stopCmd);
		} catch (IOException e) {
			getVideoControl().notifyStateChange(this, new StatusMessage(RecordState.ERROR, e.getLocalizedMessage()));
			return false;
		}
		getVideoControl().notifyStateChange(this, new StatusMessage(RecordState.IDLE, 20));
		return true;
	}

	@Override
	public boolean connect() {
		disconnect();
		connectThread = new Thread(new Runnable() {
			@Override
			public void run() {
				openSocket();
			}
		});
		connectThread.start();
		return true;
	}

	protected boolean openSocket() {
		AMPParameters ampParams = getDeviceParameters();
		try {
			tcpSocket = new Socket(ampParams.ipAddress, ampParams.port);
		} catch (IOException e) {
			getVideoControl().notifyStateChange(this, new StatusMessage(RecordState.ERROR, e.getLocalizedMessage()));
			return false;
		}
		try {
			inputStream = tcpSocket.getInputStream();
		} catch (IOException e) {
			getVideoControl().notifyStateChange(this, new StatusMessage(RecordState.ERROR, e.getLocalizedMessage()));
			return false;
		}
		try {
			outputStream = tcpSocket.getOutputStream();
			dataOutputStream = new DataOutputStream(outputStream);
		} catch (IOException e) {
			getVideoControl().notifyStateChange(this, new StatusMessage(RecordState.ERROR, e.getLocalizedMessage()));
			return false;
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
		return tcpSocket != null;
		
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
			} catch (IOException e) {
				System.out.println(e.getMessage());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("End TCP listening thread on " + getDeviceParameters().ipAddress);
	}

	@Override
	public boolean disconnect() {
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
		return ok;
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
