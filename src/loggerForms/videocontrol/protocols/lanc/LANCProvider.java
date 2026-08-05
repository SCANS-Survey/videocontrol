package loggerForms.videocontrol.protocols.lanc;

import java.awt.Window;

import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.VideoControl;
import loggerForms.videocontrol.protocols.VideoProtocol;
import loggerForms.videocontrol.protocols.VideoProtocolProvider;
import loggerForms.videocontrol.swing.dialog.ProtocolDialogPanel;

public class LANCProvider extends VideoProtocolProvider<LANCParameters> {

	public static final String providerName = "LANC (RS485 / serial)";
	@Override
	public String getName() {
		return providerName;
	}

	@Override
	public VideoProtocol getProtocol(VideoControl videoControl, DeviceParameters deviceParameters) {
		if (deviceParameters != null) {
			deviceParameters = createParameters(deviceParameters);
		}
		return new LANCProtocol(videoControl, this, deviceParameters);
	}

	@Override
	public ProtocolDialogPanel getDialogPanel(Window parent) {
		// TODO Auto-generated method stub
		return new LANCDialogPanel(this);
	}

	@Override
	public LANCParameters createParameters(DeviceParameters deviceParameters) {
		LANCParameters newParams = new LANCParameters(providerName);
		if (deviceParameters != null) {
			newParams.name = deviceParameters.name;
			newParams.recordLengthS = deviceParameters.recordLengthS;
		}
		return newParams;
	}

}
