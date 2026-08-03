package loggerForms.videocontrol.protocols;

import java.awt.Window;

import PamView.dialog.PamDialogPanel;
import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.VideoControl;

public abstract class VideoProtocol {

	private VideoControl videoControl;
	
	private VideoProtocolProvider protocolProvider;

	private DeviceParameters deviceParameters;


	/**
	 * @param videoControl
	 * @param protocolProvider
	 * @param devideParameters 
	 */
	public VideoProtocol(VideoControl videoControl, VideoProtocolProvider protocolProvider, DeviceParameters devideParameters) {
		super();
		this.videoControl = videoControl;
		this.protocolProvider = protocolProvider;
		this.deviceParameters = devideParameters;
	}
	
	/**
	 * Connect to the video system and start to monitor for messages
	 * @return
	 */
	abstract public boolean connect();
	
	/**
	 * Start recording
	 * @return true if OK
	 */
	abstract public boolean startRecording();
	
	/**
	 * Stop recording
	 * @return true if OK
	 */
	abstract public boolean stopRecording();
	
	/**
	 * Get a dialog panel
	 * @param parent
	 * @return
	 */
	abstract public PamDialogPanel getDialogPanel(Window parent);

	/**
	 * @return the videoControl
	 */
	public VideoControl getVideoControl() {
		return videoControl;
	}

	/**
	 * @return the protocolProvider
	 */
	public VideoProtocolProvider getProtocolProvider() {
		return protocolProvider;
	}
	

	/**
	 * @return the devideParameters
	 */
	public DeviceParameters getDeviceParameters() {
		return deviceParameters;
	}

	/**
	 * @param devideParameters the devideParameters to set
	 */
	public void setDeviceParameters(DeviceParameters devideParameters) {
		this.deviceParameters = devideParameters;
	}
}
