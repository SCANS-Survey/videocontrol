package loggerForms.videocontrol.protocols;

import java.awt.Window;

import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.VideoControl;
import loggerForms.videocontrol.swing.dialog.ProtocolDialogPanel;

public class AMPProvider extends VideoProtocolProvider<AMPParameters> {
	
	public static final String providerName = "Advanced Media Protocol (AMP)";

	@Override
	public String getName() {
		return providerName;
	}

	@Override
	public VideoProtocol getProtocol(VideoControl videoControl, DeviceParameters deviceParameters) {
		if (deviceParameters instanceof AMPParameters == false) {
			deviceParameters = createParameters(deviceParameters);
		}
		return new AMPProtocol(videoControl, this, deviceParameters);
	}

	@Override
	public ProtocolDialogPanel getDialogPanel(Window parent) {
		// TODO Auto-generated method stub
		return new AMPDialogPanel(this);
	}

	@Override
	public AMPParameters createParameters(DeviceParameters deviceParameters) {
		AMPParameters newParams = new AMPParameters(providerName);
		if (deviceParameters != null) {
			newParams.name = deviceParameters.name;
			newParams.recordLengthS = deviceParameters.recordLengthS;
		}
		return newParams;
	}


}
