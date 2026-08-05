package loggerForms.videocontrol.protocols.amp;

import loggerForms.videocontrol.DeviceParameters;

public class AMPParameters extends DeviceParameters {

	public AMPParameters(String providerName) {
		super(providerName);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;
	
	public String ipAddress;
	
	public int port = 3811;

}
