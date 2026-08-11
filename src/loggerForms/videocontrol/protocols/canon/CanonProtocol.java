package loggerForms.videocontrol.protocols.canon;

import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.VideoControl;
import loggerForms.videocontrol.protocols.VideoProtocol;

public class CanonProtocol extends VideoProtocol {

	private CanonProvider canonProvider;

	public CanonProtocol(VideoControl videoControl, CanonProvider protocolProvider,
			DeviceParameters deviceParameters) {
		super(videoControl, protocolProvider, deviceParameters);
		this.canonProvider = protocolProvider;
	}

	@Override
	public String connect() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String disconnect() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String startRecording() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String stopRecording() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CanonParams getDeviceParameters() {
		DeviceParameters p = super.getDeviceParameters();
		if (p instanceof CanonParams) {
			return (CanonParams) p;
		}
		else {
			return canonProvider.createParameters(p);
		}
	}

}
