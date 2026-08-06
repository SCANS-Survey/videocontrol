package loggerForms.videocontrol.protocols;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import loggerForms.LoggerForm;
import loggerForms.controls.LoggerControl;
import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.RecordState;
import loggerForms.videocontrol.StatusMessage;
import loggerForms.videocontrol.VideoControl;

public abstract class VideoProtocol {

	private VideoControl videoControl;
	
	private VideoProtocolProvider protocolProvider;

	private DeviceParameters deviceParameters;
	
	private RecordState currentState;
	
	// end time for recording in milliseconds. 
	private long recordEndTime;
	
	private Timer recordEndTimer;
	
	private Object synchObject = new Object();

	/**
	 * @param videoControl
	 * @param protocolProvider
	 * @param deviceParameters 
	 */
	public VideoProtocol(VideoControl videoControl, VideoProtocolProvider protocolProvider, DeviceParameters deviceParameters) {
		super();
		this.videoControl = videoControl;
		this.protocolProvider = protocolProvider;
		this.deviceParameters = deviceParameters;
		// create timer, but don't start it. 
		recordEndTimer = new Timer(250, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkRecordEndTime();
			}
		});
	}
	
	



	/**
	 * Connect to the video system and start to monitor for messages
	 * @return
	 */
	abstract public boolean connect();
	
	/**
	 * Disconnect to the video system and start to monitor for messages
	 * @return
	 */
	abstract public boolean disconnect();
	
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
	public void setDeviceParameters(DeviceParameters deviceParameters) {
		this.deviceParameters = deviceParameters;
	}

	/**
	 * Call from logger buttons to run an action, which will be to record for the set amount of time
	 * this should be the same for all protocols. 
	 * @param loggerForm
	 * @param loggerControl
	 * @return
	 */
	public boolean runLoggerAction(LoggerForm loggerForm, LoggerControl loggerControl) {
		long now = System.currentTimeMillis();
		DeviceParameters params = getDeviceParameters();
		synchronized (synchObject) {
			if (currentState == RecordState.RECORDING) {
				this.recordEndTime = now + params.recordLengthS*1000;
				return true;
			}
			// otherwise, always try to start, even if it's in an error state. Consider 
			// waiting a bit if it's connecting ? Later perhaps. 
			this.recordEndTime = now + params.recordLengthS*1000;
			boolean ok = startRecording();
			if (ok) {
				recordEndTimer.start();
				reportState(RecordState.RECORDING, "", params.recordLengthS);
			}
			else {
				String err = String.format("Unable to start %s using %s protocol", params.name, protocolProvider.getName());
				reportState(RecordState.ERROR, err, 0);
			}
			
		}
		return false;
	}

	
	protected void checkRecordEndTime() {
		long now = System.currentTimeMillis();
		if (now >= recordEndTime) {
			synchronized (synchObject) {
				stopRecording();
				reportState(RecordState.IDLE, "", 0);
				recordEndTimer.stop();
			}
		}
			else {
				int remaining = (int) ((recordEndTime-now)/1000);
				reportState(RecordState.RECORDING, "", remaining);
			}
		
	}
	
	/**
	 * Set the state and pass on a message to the rest of the system. 
	 * Messages and state setting can be done separately, but this is easier. 
	 * @param state
	 * @param message
	 * @param remainingS
	 */
	public void reportState(RecordState state, String message, int remainingS) {
		this.currentState = state;
		StatusMessage sm = new StatusMessage(state, message, remainingS);
		getVideoControl().notifyStateChange(this, sm);
	}

	public RecordState getCurrentState() {
		return currentState;
	}

	public void setCurrentState(RecordState currentState) {
		this.currentState = currentState;
	}
}
