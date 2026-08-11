package loggerForms.videocontrol.protocols.lumix;

import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.protocols.ShootMode;

public class LumixParameters extends DeviceParameters {

	public static final long serialVersionUID = 1L;
	
	public String ipAddress = "";
	
	private ShootMode shootMode = ShootMode.VIDEO;
	
	public LumixParameters(String providerName) {
		super(providerName);
	}

	public ShootMode getShootMode() {
		if (shootMode == null) {
			shootMode = ShootMode.VIDEO;
		}
		return shootMode;
	}

	public void setShootMode(ShootMode shootMode) {
		this.shootMode = shootMode;
	}
	

}
