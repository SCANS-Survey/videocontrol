package loggerForms.videocontrol.protocols.amp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;



public class AMPTest {


	private String ipAddr = "192.168.2.230";
	private int ipPort = 3811;
	private Socket tcpSock;
	private DataOutputStream dataOut;
	private InputStream inputStream;
	private static boolean verbose = true;

//	String[] cmds = {"CRAT0007204Vtr1\n", "CMDS00042002\n", "CMDS00042000\n", "CMDS0004A026\n", "STOP0000\n"};	
		String[] cmds = {"CRAT0007204Vtr1\n", "CMDS0004A026\n","CMDS0004AA14\n","CMDS0004A015\n","STOP0000\n"};

	public static void main(String[] args) {

		AMPTest et = new AMPTest();
				et.runTCP();
//		et.runUDP();

	}

	public void runUDP() {
		System.out.println("Default charset is " + Charset.defaultCharset());

		try {
			InetAddress address = InetAddress.getByName(ipAddr);
	        DatagramSocket datagramSocket = new DatagramSocket();
	        datagramSocket.setSoTimeout(1000);
	        byte[] rxData = new byte[256];
			DatagramPacket  dg;
			for (int i = 0; i < cmds.length; i++) {
				System.out.printf("Send " + cmds[i]);
				byte[] buff = cmds[i].getBytes(Charset.defaultCharset());
				dg = new DatagramPacket(buff, buff.length, address, ipPort);
				datagramSocket.send(dg);
				if (i == 1) {
					pause(3000);
				}
				DatagramPacket rp = new DatagramPacket(rxData, rxData.length);
				try {
				datagramSocket.receive(rp);
				System.out.println(new String(rxData, rp.getLength()));
				}
				catch (SocketTimeoutException te) {
					System.out.println("Timeout");
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Simple test - open a tcpip port to Shogun Studio, write some basic commands and hope to get 
	 * something back.  
	 * REturn codes 1001 seems to be good, 111201 bad
	 * Receive reply, which could be 1001 (ACK) or 1111 (NAK) or 2222 (ERR).
	 */
	public void runTCP() {
		if (!openSocket(ipAddr, ipPort)) {
			return;
		}
		//		String cmd = "CRAT0007204Vtr1\nCMDS00042000\n";			
		//		cmd = "STOP\n";

		for (int i = 0; i < cmds.length; i++) {
			String cmd = cmds[i];
			//			System.out.printf("Write String \"%s\" ", cmd);
			writeString(cmd);
			pause(1000);
			byte[] dataIn = readData();
			displayData(dataIn);
			if (i==1) {
				System.out.println("          pause");
				pause(3000);
			}
		}
		pause(100);

		//		byte[] dataIn = readData();
		//		displayData(dataIn);


		closeSocket();

	}

	String toHexString(byte[] data) {
		String str = "";
		for (int i = 0; i < data.length; i++) {
			str += String.format("%02X", data[i]);
		}
		return str;
	}

	private void displayData(byte[] dataIn) {
		if (dataIn == null) {
			System.out.println("No data returned");
		}
		else {
			System.out.printf("\nReturned %d bytes 0x:", dataIn.length);
			for (int i = 0; i < dataIn.length; i++) {
				System.out.printf("%02x", dataIn[i]);
			}
			//			String str = String.new String(dataIn);
			//			System.out.printf(", as string: \"%s\"", dataIn);
			System.out.printf(" as String: %s\n", new String(dataIn));
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
				data = inputStream.readAllBytes();
			}
			return data;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	private boolean writeString(String str) {
		System.out.printf("Send command %s as 0X\"%s\"", str, toHexString(str.getBytes(Charset.defaultCharset())));
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

			tcpSock.setSoTimeout(1000);

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
