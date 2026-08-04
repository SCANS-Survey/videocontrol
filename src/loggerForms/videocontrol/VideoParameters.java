package loggerForms.videocontrol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import loggerForms.videocontrol.protocols.AMPParameters;

public class VideoParameters implements Cloneable, Serializable {

	public static final long serialVersionUID = 1L;
	
	private ArrayList<DeviceParameters> deviceParameters = new ArrayList();
	
	/**
	 * @return the deviceParameters
	 */
	public ArrayList<DeviceParameters> getDeviceParameters() {
		return deviceParameters;
	}

	/**
	 * Get the device params for a given name. 
	 * @param devName
	 * @return
	 */
	public DeviceParameters getDeviceParameters(String devName) {
		for (DeviceParameters dp : deviceParameters) {
			if (dp.name.equals(devName)) {
				return dp;
			}
		}
		return null;
	}
	
	/**
	 * Set device params, replacing any with the same name. 
	 * @param deviceParameters
	 */
	public void setDeviceParameters(DeviceParameters deviceParameters) {
		removeDeviceParameters(deviceParameters.name);
		this.deviceParameters.add(deviceParameters);
	}

	/**
	 * Remove device params of given name. 
	 * @param name
	 * @return removed params
	 */
	public DeviceParameters removeDeviceParameters(String name) {		
		Iterator<DeviceParameters> it = this.deviceParameters.iterator();
		while (it.hasNext()) {
			DeviceParameters dp = it.next();
			if (dp.name.equals(name)) {
				it.remove();
				return dp;
			}
		}
		return null;
	}
}
