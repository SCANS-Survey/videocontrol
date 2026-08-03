package loggerForms.videocontrol.protocols;

import java.awt.Window;

import PamView.dialog.PamDialogPanel;
import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.VideoControl;

public class LANCProtocol extends VideoProtocol {

	public LANCProtocol(VideoControl videoControl, VideoProtocolProvider protocolProvider, DeviceParameters devideParameters) {
		super(videoControl, protocolProvider, devideParameters);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean connect() {
		// TODO Auto-generated method stub
		return false;
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
	public PamDialogPanel getDialogPanel(Window parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LANCParameters getDeviceParameters() {
		DeviceParameters params = super.getDeviceParameters();
		if (params instanceof LANCParameters) {
			return (LANCParameters) params;
		}
		else {
			return new LANCParameters(getProtocolProvider().getName());
		}
	}

}
