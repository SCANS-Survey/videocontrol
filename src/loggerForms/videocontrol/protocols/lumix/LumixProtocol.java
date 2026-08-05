package loggerForms.videocontrol.protocols.lumix;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.VideoControl;
import loggerForms.videocontrol.protocols.VideoProtocol;
import loggerForms.videocontrol.protocols.VideoProtocolProvider;

/**
 * Control of Panasonic Lumix camera. 
 * Broadly following the Python examples at https://github.com/palmdalian/python_lumix_control/blob/master/lumix_control.py
 * using the Java HttpRequest library: https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpRequest.html
 * Examples at https://openjdk.org/groups/net/httpclient/recipes.html
 */
public class LumixProtocol extends VideoProtocol {

	private HttpClient client;

	public LumixProtocol(VideoControl videoControl, VideoProtocolProvider protocolProvider,
			DeviceParameters devideParameters) {
		super(videoControl, protocolProvider, devideParameters);
	}

	@Override
	public boolean connect() {
		client = HttpClient.newHttpClient();
		return true;
	}

	@Override
	public boolean disconnect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean startRecording() {
		   HttpRequest request = HttpRequest.newBuilder()
			         .uri(URI.create("http://foo.com/"))
			         .build();
			   client.sendAsync(request, BodyHandlers.ofString())
			         .thenApply(HttpResponse::body)
			         .thenAccept(System.out::println)
			         .join(); 
		return false;
	}

	@Override
	public boolean stopRecording() {
		// TODO Auto-generated method stub
		return false;
	}

}
