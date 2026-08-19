package loggerForms.videocontrol.protocols.canon;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.fasterxml.jackson.databind.JsonNode;

import java.net.URI;

public class CanonTest {

	// see https://developercommunity.usa.canon.com/s/article/CCAPI-Function-List
	public static void main(String[] args) {
		new CanonTest().run();
	}

	private static String ipAddr = "192.168.0.101";
	int port = 49152;
	String setFile = "canon-settings.json";
	private void run() {
		CCAPI ccapi = null;
		
		try {
			ccapi = new CCAPI(ipAddr, port, true, setFile);
			JsonNode temp = ccapi.getTemperature();
			System.out.println(temp);
			ccapi.shoot(null);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void run2() {
	    String CAMERA_IP = ipAddr; 
	    String CCAPI_BASE_URL = "http://" + CAMERA_IP + "/ccapi/ver100";
	    // 1. Build the endpoint URL for shooting/taking a photo
//        String shootEndpoint = CCAPI_BASE_URL + "/action/shoot";
        String shootEndpoint = CCAPI_BASE_URL + "/devicestatus/storage";
        
        // 2. Prepare the JSON payload required by Canon's API specifications
        // "full" drives the focus, tracking, and instantly releases the shutter
        String jsonPayload = "{\"action\": \"full\", \"af\": true}";

        // 3. Create the HTTP Client with a timeout configuration
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        // 4. Formulate the HTTP POST request
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(shootEndpoint))
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
//                .build();
        HttpRequest getrequest = HttpRequest.newBuilder()
                .uri(URI.create(shootEndpoint))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        System.out.println("Sending shutter command to: " + shootEndpoint);
        System.out.println(getrequest);

        // 5. Send the request and handle the response asynchronously or synchronously
        try {
            HttpResponse<String> response = client.send(getrequest, HttpResponse.BodyHandlers.ofString());
            
            // Log response metrics
            System.out.println("HTTP Status Code: " + response.statusCode());
            System.out.println("Camera Response Body: " + response.body());
            
            if (response.statusCode() == 200) {
                System.out.println("Success: Photo captured successfully!");
            } else {
                System.out.println("Error: Device returned an error configuration.");
            }
            
        } catch (Exception e) {
            System.err.println("Failed to communicate with the Canon camera.");
            e.printStackTrace();
        }
	}
	/**
	 * Test code from google search:
	 * import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class CanonCameraControl {

    // Replace with your camera's actual network IP address
    private static final String CAMERA_IP = "192.168.1.100"; 
    private static final String CCAPI_BASE_URL = "http://" + CAMERA_IP + "/ccapi/ver100";

    public static void main(String[] args) {
        // 1. Build the endpoint URL for shooting/taking a photo
        String shootEndpoint = CCAPI_BASE_URL + "/action/shoot";
        
        // 2. Prepare the JSON payload required by Canon's API specifications
        // "full" drives the focus, tracking, and instantly releases the shutter
        String jsonPayload = "{\"action\": \"full\", \"af\": true}";

        // 3. Create the HTTP Client with a timeout configuration
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        // 4. Formulate the HTTP POST request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(shootEndpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        System.out.println("Sending shutter command to: " + shootEndpoint);

        // 5. Send the request and handle the response asynchronously or synchronously
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Log response metrics
            System.out.println("HTTP Status Code: " + response.statusCode());
            System.out.println("Camera Response Body: " + response.body());
            
            if (response.statusCode() == 200) {
                System.out.println("Success: Photo captured successfully!");
            } else {
                System.out.println("Error: Device returned an error configuration.");
            }
            
        } catch (Exception e) {
            System.err.println("Failed to communicate with the Canon camera.");
            e.printStackTrace();
        }
    }
}

	 */

}
