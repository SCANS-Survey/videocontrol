package loggerForms.videocontrol;

/**
 * Status messages to send around from the recorder with state and record status. 
 */
public class StatusMessage {

	private RecordState recordState;
	
	private String message;
	
	private int remaining;

	/**
	 * @param recordState
	 * @param message
	 * @param remaining
	 */
	public StatusMessage(RecordState recordState, String message, int remaining) {
		super();
		this.recordState = recordState;
		this.message = message;
		this.remaining = remaining;
	}

	/**
	 * @param recordState
	 * @param message
	 */
	public StatusMessage(RecordState recordState, String message) {
		super();
		this.recordState = recordState;
		this.message = message;
	}

	/**
	 * @param recordState
	 * @param remaining
	 */
	public StatusMessage(RecordState recordState, int remaining) {
		super();
		this.recordState = recordState;
		this.remaining = remaining;
	}

	/**
	 * @return the recordState
	 */
	public RecordState getRecordState() {
		return recordState;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the remaining
	 */
	public int getRemaining() {
		return remaining;
	}

	@Override
	public String toString() {
		switch (recordState) {
		case RECORDING:
			return String.format("Recording %ds remaining", remaining);
		case ERROR:
		case IDLE:
		case CONNECTING:
			String msg = recordState.toString();
			if (message != null) {
				msg += ": " + message;
			}
			return msg;
		}
		return super.toString();
	}
	
	
}
