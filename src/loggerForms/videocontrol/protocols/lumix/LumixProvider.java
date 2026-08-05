package loggerForms.videocontrol.protocols.lumix;

import java.awt.Window;

import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.VideoControl;
import loggerForms.videocontrol.protocols.VideoProtocol;
import loggerForms.videocontrol.protocols.VideoProtocolProvider;
import loggerForms.videocontrol.swing.dialog.ProtocolDialogPanel;

public class LumixProvider extends VideoProtocolProvider<LumixParameters> {

	public static final String _providerName = "Panasonic Lumix";
	
	@Override
	public String getName() {
		return _providerName;
	}

	@Override
	public VideoProtocol getProtocol(VideoControl videoControl, DeviceParameters deviceParameters) {
		return new LumixProtocol(videoControl, this, deviceParameters);
	}

	@Override
	public ProtocolDialogPanel getDialogPanel(Window parent) {
		// TODO Auto-generated method stub
		return new LumixDialogPanel(this);
	}

	@Override
	public LumixParameters createParameters(DeviceParameters deviceParameters) {
		return new LumixParameters(_providerName);
	}

}
