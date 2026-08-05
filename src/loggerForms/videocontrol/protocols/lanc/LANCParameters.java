package loggerForms.videocontrol.protocols.lanc;

import loggerForms.videocontrol.DeviceParameters;

public class LANCParameters extends DeviceParameters {


	
	public LANCParameters(String providerName) {
		super(providerName);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;

	public String port = "";
	
	public int bitRate = 9600;
	
}
