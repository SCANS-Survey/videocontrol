package loggerForms.videocontrol.protocols;

import loggerForms.videocontrol.VideoControl;

public class AMPProvider extends VideoProtocolProvider {

	@Override
	public String getName() {
		return "Advanced Media Protocol (AMP)";
	}

	@Override
	public VideoProtocol getProtocol(VideoControl videoControl) {
		return new AMPProtocol(videoControl, this, new AMPParameters(getName()));
	}

}
