package loggerForms.videocontrol;

public enum RecordState {
	IDLE, RECORDING, ERROR, CONNECTING;

	@Override
	public String toString() {
		switch (this) {
		case ERROR:
			return "Error";
		case IDLE:
			return "Idle";
		case RECORDING:
			return "Recording";
		case CONNECTING:
			return "Connecting";
		default:
			break;

		}
		return null;
	}

}
