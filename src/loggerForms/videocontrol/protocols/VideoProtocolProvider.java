package loggerForms.videocontrol.protocols;

import java.awt.Window;

import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.VideoControl;
import loggerForms.videocontrol.swing.dialog.ProtocolDialogPanel;

public abstract class VideoProtocolProvider<T extends DeviceParameters> {

	public abstract String getName();
	
	public abstract VideoProtocol getProtocol(VideoControl videoControl, DeviceParameters deviceParameters);
	
	/**
	 * Get a dialog panel
	 * @param parent
	 * @return
	 */
	abstract public ProtocolDialogPanel getDialogPanel(Window parent);
	

	/**
	 * Create a new parameter set. If the passed params are not null
	 * copy over the name field only. 
	 * @param deviceParameters
	 * @return
	 */
	abstract public T createParameters(DeviceParameters deviceParameters);

	@Override
	public String toString() {
		return getName();
	}
	
}
