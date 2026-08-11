package loggerForms.videocontrol.protocols.lumix;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.RecordState;
import loggerForms.videocontrol.StatusMessage;
import loggerForms.videocontrol.VideoControl;
import loggerForms.videocontrol.protocols.ShootMode;
import loggerForms.videocontrol.protocols.VideoProtocol;
import loggerForms.videocontrol.protocols.VideoProtocolProvider;

/**
 * Control of Panasonic Lumix camera. 
 * Broadly following the Python examples at https://github.com/palmdalian/python_lumix_control/blob/master/lumix_control.py
 * using the Java HttpRequest library: https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpRequest.html
 * Examples at https://openjdk.org/groups/net/httpclient/recipes.html
 */
public class LumixProtocol extends VideoProtocol {

	private LumixCameraControl lumixCamera;
	
	private volatile Thread connectThread;

	private boolean connectionOk;

	public LumixProtocol(VideoControl videoControl, VideoProtocolProvider protocolProvider,
			DeviceParameters devideParameters) {
		super(videoControl, protocolProvider, devideParameters);
	}

	@Override
	public String connect() {
		LumixParameters params = getDeviceParameters();
		lumixCamera = new LumixCameraControl(params.ipAddress);

		connectionOk = lumixCamera.startCameraControl();
		if (connectionOk == false) {
			return "Can't connect";
		}
		
//		if (connectThread != null) {
//			connectThread.interrupt();
//		}
//		getVideoControl().notifyStateChange(LumixProtocol.this, new StatusMessage(RecordState.CONNECTING, null));
//		connectThread = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				boolean ok = lumixControl.startCameraControl();
//				if (ok == false) {
//					getVideoControl().notifyStateChange(LumixProtocol.this, new StatusMessage(RecordState.ERROR, "Can't connect"));
//				}
//				else {
//					getVideoControl().notifyStateChange(LumixProtocol.this, new StatusMessage(RecordState.IDLE, null));
//				}
//				connectThread = null;
//			}
//		});
//		connectThread.start();
		
		return "Connecting";
	}

	@Override
	public String disconnect() {
		lumixCamera.videoRecordStop();
		lumixCamera.stopCapture();
		return null;
	}

	@Override
	public LumixParameters getDeviceParameters() {
		DeviceParameters deviceParameters = super.getDeviceParameters();
		if (deviceParameters instanceof LumixParameters) {
			return (LumixParameters) deviceParameters;
		}
		else {
			return new LumixParameters(LumixProvider._providerName);
		}
	}

	@Override
	public void setDeviceParameters(DeviceParameters deviceParameters) {
		if (deviceParameters instanceof LumixParameters) {
			super.setDeviceParameters(deviceParameters);
		}
		else {
			super.setDeviceParameters(new LumixParameters(LumixProvider._providerName));
		}
	}

	private boolean checkConnection() {
		if (connectionOk == false) {
			connect();
		}
		return connectionOk;
	}
	
	@Override
	public String startRecording() {
		if (checkConnection() == false) {
			return "Connection Error";
		}
		ShootMode mode = getDeviceParameters().getShootMode();
		String response = null;
		switch(mode) {
		case STILL:
			response = lumixCamera.capturePhoto();
			break;
		case VIDEO:
			response = lumixCamera.videoRecordStart();
			break;
		default:
			return "Unknown shoot mode";
		}
		boolean ok = lumixCamera.checkResponse(response);
		return ok ? null : "Error starting";
	}

	@Override
	public String stopRecording() {
		if (checkConnection() == false) {
			return "Connection Error";
		}
		ShootMode mode = getDeviceParameters().getShootMode();
		String response = null;
		switch(mode) {
		case STILL:
			response = lumixCamera.stopCapture();
			break;
		case VIDEO:
			response = lumixCamera.videoRecordStop();
			break;
		}
		boolean ok = lumixCamera.checkResponse(response);
		return ok ? null : "Error stopping";
	}

}
