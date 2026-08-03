package loggerForms.videocontrol;

import java.io.Serializable;
import java.util.TreeMap;

import loggerForms.videocontrol.protocols.AMPParameters;

public class VideoParameters implements Cloneable, Serializable {

	public static final long serialVersionUID = 1L;
	
	private TreeMap<String, DeviceParameters> deviceParameters = new TreeMap();
	
	/**
	 * @return the deviceParameters
	 */
	public TreeMap<String, DeviceParameters> getDeviceParameters() {
		return deviceParameters;
	}

	public DeviceParameters getDeviceParameters(String devName) {
		return deviceParameters.get(devName);
	}
	
	public void setDeviceParameters(DeviceParameters deviceParameters) {
		this.deviceParameters.put(deviceParameters.name, deviceParameters);
	}

	public void removeDeviceParameters(String name) {
		this.deviceParameters.remove(name);
	}
}
