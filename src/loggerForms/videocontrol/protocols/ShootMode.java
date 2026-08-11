package loggerForms.videocontrol.protocols;

/**
 * Shooting modes. May depend a lot on camera setup, e.g. is is 
 * set for bust shooting, or single shot. Some cameras may allow
 * control over this ? 
 */
public enum ShootMode {
	
	STILL, VIDEO;

	@Override
	public String toString() {
		switch (this) {
		case STILL:
			return "Still images";
		case VIDEO:
			return "Video";
		default:
			break;
		}
		return null;
	}
	
	
}
