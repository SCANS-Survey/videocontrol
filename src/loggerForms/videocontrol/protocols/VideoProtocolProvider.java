package loggerForms.videocontrol.protocols;

import loggerForms.videocontrol.VideoControl;

public abstract class VideoProtocolProvider {

	public abstract String getName();
	
	public abstract VideoProtocol getProtocol(VideoControl videoControl);

	@Override
	public String toString() {
		return getName();
	}
	
}
