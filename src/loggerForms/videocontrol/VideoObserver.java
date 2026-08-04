package loggerForms.videocontrol;

import loggerForms.videocontrol.protocols.VideoProtocol;

/**
 * Receive notifications when things change
 */
public interface VideoObserver {

	/**
	 * configuration has changed. 
	 */
	public void configurationChange();
	
	/**
	 * State of one of the recorders has changed. 
	 * @param videoProtocol
	 */
	public void stateChange(VideoProtocol videoProtocol, StatusMessage statusMessage);
	
}
