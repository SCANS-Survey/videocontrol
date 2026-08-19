package loggerForms.videocontrol.protocols.canon;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.simtechdata.waifupnp.UPnP;



public class CanonDiscover{

	private static final int PORT = 4137;


	public static void main(String args[]) {
		CanonDiscover cd = new CanonDiscover();
		//		cd.testWaif();
		cd.testDIY();
		//		cd.testJUPnP();
	}

	private boolean debug = true;
	private HttpClient httpClient;
	private void testDIY() {  

	    this.httpClient = HttpClient.newHttpClient();
	    
		int port = 1900;
		String multicastAddress = "239.255.255.250";

		try (MulticastSocket socket = new MulticastSocket(port)) {
			InetAddress group = InetAddress.getByName(multicastAddress);
			socket.joinGroup(group);

			System.out.println("Listening for multicast messages...");
			byte[] buffer = new byte[1024];

			while (true) {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				/**
				 * Canon discovery packet looks like this:
Received: NOTIFY * HTTP/1.1
Host: 239.255.255.250:1900
Cache-Control: max-age=1800
Location: http://192.168.0.101:49152/upnp/CameraDevDesc.xml
NT: urn:schemas-canon-com:service:ICPO-WFTEOSSystemService:1
NTS: ssdp:alive
Server: Camera OS/1.0 UPnP/1.0 Canon Device Discovery/1.0
USN: uuid:00000000-0000-0000-0001-F8A26DB217D8::urn:schemas-canon-com:service:ICPO-WFTEOSSystemService:1
				 */

				String message = new String(packet.getData(), 0, packet.getLength());
				if (message.contains("Canon Device Discovery")) {
					System.out.println("\nReceived: " + message);
					if (discoverCamera(message)) {
						break;
					};
				}
				else {
					System.out.printf(".");
				}
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	private boolean discoverCamera(String message) {
		String[] lines = message.split("\n");
		System.out.println("Message line: " + lines.length);
		String location = findLine(lines, "Location:");
		if (location == null) {
			return false;
		}
		String address = location.substring(0, location.indexOf("/upnp"));
		System.out.printf("\"%s\"\n", location);
		HttpResponse<String> response = null;
		
		try {
			response = _getRaw(location, null);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		System.out.println(response.body());
		
		
		
		return true;
		
	}
	
	 private HttpResponse<String> _getRaw(String host, String path) throws IOException, InterruptedException {
	        String url;
	        if (host.startsWith("http")) {
	        	url = host;
	        }
	        else if (path == null) {
	        	url = String.format("http://%s", host);
	        }
	        else {
	        	url = String.format("http://%s/ccapi/ver110/%s", host, path);
	        }
	        if (debug) System.out.println("GET: " + url);
	        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
	        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	    }
	
	private String findLine(String[] lines, String string) {
		if (lines == null) {
			return null;
		}
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith(string)) {
				return lines[i].substring(string.length()).trim();
			}
		}
		return null;
	}
	private void testJUPnP() {
		// TODO Auto-generated method stub
		//		RegistryListe


	}

	//	private class JPListener extends Re 

	private void testWaif() {
		try {
			System.out.println("Opening socket...");
			final ServerSocket ss = new ServerSocket(PORT); //starts listening on port TCP 4137
			System.out.println("Socket opened");            
			//create a new thread that listens for incoming conenctions
			new Thread() {
				@Override
				public void run() {
					for (;;) {
						try {
							Socket s = ss.accept(); //wait for connections on socket
							System.out.println("Incoming connection from " + s.getInetAddress().getHostAddress()); //print remote machine IP
							s.close(); //close the connection
						} catch (Throwable t) {
							System.err.println("Network error: "+t);
						}
					}
				}
			}.start();
			//meanwhile, we try to open the port on the local gateway
			System.out.println("Attempting UPnP port forwarding... ");
			System.out.println("Default gateway is " + UPnP.getDefaultGatewayIP());
			if (UPnP.isUPnPAvailable()) { //is UPnP available?
				if (UPnP.isMappedTCP(PORT)) { //is the port already mapped?
					System.out.println("UPnP port forwarding not enabled: port is already mapped");
				} else if (UPnP.openPortTCP(PORT)) { //try to map port
					System.out.println("UPnP port forwarding enabled");
				} else {
					System.out.println("UPnP port forwarding failed");
				}
			} else {
				System.out.println("UPnP is not available");
			}

		} catch (Throwable t) {
			System.err.println("Network error: "+t);
		}

	}



}
