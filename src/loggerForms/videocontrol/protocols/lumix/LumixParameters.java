package loggerForms.videocontrol.protocols.lumix;

import loggerForms.videocontrol.DeviceParameters;

public class LumixParameters extends DeviceParameters {

	public static final long serialVersionUID = 1L;
	
	public String ipAddress = "";
	
	public LumixParameters(String providerName) {
		super(providerName);
	}
	

}
