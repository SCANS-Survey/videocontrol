package loggerForms.videocontrol;

import loggerForms.LoggerForm;
import loggerForms.actions.LoggerAction;
import loggerForms.controls.LoggerControl;
import loggerForms.videocontrol.protocols.VideoProtocol;

public class VideoButtonAction extends LoggerAction {

	private VideoProtocol videoProtocol;

	public VideoButtonAction(VideoControl videoControl, VideoProtocol videoProtocol, String name, String description) {
		super(videoControl, name, description);
		this.videoProtocol = videoProtocol;
	}
	
	/**
	 * convenience method to call the constructor with suitable names for everything
	 * @param videoControl
	 * @param videoProtocol
	 * @return
	 */
	public static VideoButtonAction createAction(VideoControl videoControl, VideoProtocol videoProtocol) {
		DeviceParameters params = videoProtocol.getDeviceParameters();
		String name = videoControl.getUnitName() + ": record " + params.name;
		String description = String.format("Record %s camera for %ds using %s protocol", params.name, params.recordLengthS, videoProtocol.getProtocolProvider().getName()); 
		return new VideoButtonAction(videoControl, videoProtocol, name, description);
	}

	@Override
	public boolean runAction(LoggerForm loggerForm, LoggerControl loggerControl) {
		return videoProtocol.runLoggerAction(loggerForm, loggerControl);
	}

}
