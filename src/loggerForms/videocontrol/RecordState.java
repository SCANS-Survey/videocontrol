package loggerForms.videocontrol;

public enum RecordState {
	IDLE, RECORDING, ERROR;

	@Override
	public String toString() {
		switch (this) {
		case ERROR:
			return "Error";
		case IDLE:
			return "Idle";
		case RECORDING:
			return "Recording";
		default:
			break;

		}
		return null;
	}

}
