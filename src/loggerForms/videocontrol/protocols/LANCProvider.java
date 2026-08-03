package loggerForms.videocontrol.protocols;

import loggerForms.videocontrol.VideoControl;

public class LANCProvider extends VideoProtocolProvider {

	@Override
	public String getName() {
		return "LANC (RS485 / serial)";
	}

	@Override
	public VideoProtocol getProtocol(VideoControl videoControl) {
		return new LANCProtocol(videoControl, this, new LANCParameters(getName()));
	}

}
