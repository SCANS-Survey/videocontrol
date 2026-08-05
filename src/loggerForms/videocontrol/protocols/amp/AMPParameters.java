package loggerForms.videocontrol.protocols.amp;

import loggerForms.videocontrol.DeviceParameters;

public class AMPParameters extends DeviceParameters {

	private static final long serialVersionUID = 1L;
	
	public AMPParameters(String providerName) {
		super(providerName);
		// TODO Auto-generated constructor stub
	}
	
	public String ipAddress;
	
	public int port = 3811;

}
