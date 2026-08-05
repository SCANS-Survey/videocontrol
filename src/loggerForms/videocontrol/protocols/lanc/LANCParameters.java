package loggerForms.videocontrol.protocols.lanc;

import loggerForms.videocontrol.DeviceParameters;

public class LANCParameters extends DeviceParameters {

	private static final long serialVersionUID = 1L;
	
	public LANCParameters(String providerName) {
		super(providerName);
		// TODO Auto-generated constructor stub
	}

	public String port = "";
	
	public int bitRate = 9600;
	
}
