package loggerForms.videocontrol.protocols;

import java.awt.Window;

import PamView.dialog.PamDialogPanel;
import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.VideoControl;

public class AMPProtocol extends VideoProtocol {

	public AMPProtocol(VideoControl videoControl, VideoProtocolProvider protocolProvider, DeviceParameters deviceParameters) {
		super(videoControl, protocolProvider, deviceParameters);
	}

	@Override
	public boolean startRecording() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stopRecording() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean connect() {
		// TODO Auto-generated method stub
		return false;
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

	@Override
	public PamDialogPanel getDialogPanel(Window parent) {
		return new AMPDialogPanel(this, getDeviceParameters());
	}

}
