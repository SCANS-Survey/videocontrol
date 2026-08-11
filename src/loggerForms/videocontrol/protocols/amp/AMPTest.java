package loggerForms.videocontrol.protocols.amp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;


public class AMPTest {


	private String ipAddr = "192.168.2.230";
	private int ipPort = 3811;
	private Socket tcpSock;
	private DataOutputStream dataOut;
	private InputStream inputStream;
	private static boolean verbose = true;

	public static void main(String[] args) {

		AMPTest et = new AMPTest();
		et.run();

	}

	/**
	 * Simple test - open a tcpip port to Shogun Studio, write some basic commands and hope to get 
	 * something back.  
	 */
	public void run() {
		if (!openSocket(ipAddr, ipPort)) {
			return;
		}
		//		String cmd = "CRAT0007204Vtr1\nCMDS00042000\n";			
		String[] cmds = {"CRAT0007204Vtr1\n", "CMDS00042002\n", "CMDS00042000\n"};		
		//		cmd = "STOP\n";

		for (int i = 0; i < cmds.length; i++) {
			String cmd = cmds[i];
			System.out.printf("Write String \"%s\" ", cmd);
			writeString(cmd);
			pause(100);
			byte[] dataIn = readData();
			displayData(dataIn);
			if (i>0) {
				System.out.println("          pause");
				pause(3000);
			}
		}
		pause(100);

		byte[] dataIn = readData();
		displayData(dataIn);


		closeSocket();

	}

	private void displayData(byte[] dataIn) {
		if (dataIn == null) {
			System.out.println("No data returned");
		}
		else {
			System.out.printf("Returned %d bytes 0x:", dataIn.length);
			for (int i = 0; i < dataIn.length; i++) {
				System.out.printf("%02x", dataIn[i]);
			}
//			String str = String.new String(dataIn);
//			System.out.printf(", as string: \"%s\"", dataIn);
			System.out.printf("\n");
		}
	}

	private void pause(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private byte[] readData() {
		try {
			int bytesAvail = inputStream.available();
			byte[] data = null;
			if (bytesAvail > 0) {
				data = new byte[bytesAvail];
				inputStream.read(data);
			}
			return data;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	private boolean writeString(String str) {
		try {
			dataOut.writeBytes(str);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean writeData(byte[] data) {
		try {
			dataOut.write(data);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean openSocket(String ipAddr, int ipPort) {
		try {
			tcpSock = new Socket(ipAddr, ipPort);
			// create a data output stream. 
			dataOut = new DataOutputStream(tcpSock.getOutputStream());
			// and get the input stream
			inputStream = tcpSock.getInputStream();

			System.out.printf("TCPIP port %d open on %s, connectionStatus = %s\n", ipPort, ipAddr, tcpSock.isConnected());
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}


		return true;
	}

	private void closeSocket() {
		// close the socket
		try {
			inputStream.close();
			dataOut.close();
			tcpSock.close();
			System.out.println("TCP socket closed");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
