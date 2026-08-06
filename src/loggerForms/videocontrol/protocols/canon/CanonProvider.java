package loggerForms.videocontrol.protocols.canon;

import java.awt.Window;

import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.VideoControl;
import loggerForms.videocontrol.protocols.VideoProtocol;
import loggerForms.videocontrol.protocols.VideoProtocolProvider;
import loggerForms.videocontrol.swing.dialog.ProtocolDialogPanel;

public class CanonProvider extends VideoProtocolProvider<CanonParams> {

	public static final String _providerName = "Canon CCAPI";
	
	@Override
	public String getName() {
		return _providerName;
	}

	@Override
	public VideoProtocol getProtocol(VideoControl videoControl, DeviceParameters deviceParameters) {
		return new CanonProtocol(videoControl, this, deviceParameters);
	}

	@Override
	public ProtocolDialogPanel getDialogPanel(Window parent) {
		return new CanonDialogPanel(this);
	}

	@Override
	public CanonParams createParameters(DeviceParameters deviceParameters) {
		if (deviceParameters instanceof CanonParams) {
			return (CanonParams) deviceParameters;
		}
		CanonParams canonParams = new CanonParams(_providerName);
		if (deviceParameters != null) {
			canonParams.name = deviceParameters.name;
			canonParams.recordLengthS = deviceParameters.recordLengthS;
		}
		return canonParams;
	}

}
