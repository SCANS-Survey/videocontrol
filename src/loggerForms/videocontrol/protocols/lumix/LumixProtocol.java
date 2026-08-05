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
import loggerForms.videocontrol.protocols.VideoProtocol;
import loggerForms.videocontrol.protocols.VideoProtocolProvider;

/**
 * Control of Panasonic Lumix camera. 
 * Broadly following the Python examples at https://github.com/palmdalian/python_lumix_control/blob/master/lumix_control.py
 * using the Java HttpRequest library: https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpRequest.html
 * Examples at https://openjdk.org/groups/net/httpclient/recipes.html
 */
public class LumixProtocol extends VideoProtocol {

	private LumixCameraControl lumixControl;
	
	private volatile Thread connectThread;

	public LumixProtocol(VideoControl videoControl, VideoProtocolProvider protocolProvider,
			DeviceParameters devideParameters) {
		super(videoControl, protocolProvider, devideParameters);
	}

	@Override
	public boolean connect() {
		LumixParameters params = getDeviceParameters();
		lumixControl = new LumixCameraControl(params.ipAddress);
		if (connectThread != null) {
			connectThread.interrupt();
		}
		getVideoControl().notifyStateChange(LumixProtocol.this, new StatusMessage(RecordState.CONNECTING, null));
		connectThread = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean ok = lumixControl.startCameraControl();
				if (ok == false) {
					getVideoControl().notifyStateChange(LumixProtocol.this, new StatusMessage(RecordState.ERROR, "Can't connect"));
				}
				else {
					getVideoControl().notifyStateChange(LumixProtocol.this, new StatusMessage(RecordState.IDLE, null));
				}
				connectThread = null;
			}
		});
		connectThread.start();
		
		return true;
	}

	@Override
	public boolean disconnect() {
		// TODO Auto-generated method stub
		return false;
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

	@Override
	public boolean startRecording() {
		String resp = lumixControl.videoRecordStart();
		boolean ok = lumixControl.checkResponse(resp);
		return ok;
	}

	@Override
	public boolean stopRecording() {
		String resp = lumixControl.videoRecordStop();
		boolean ok = lumixControl.checkResponse(resp);
		return ok;
	}

}
