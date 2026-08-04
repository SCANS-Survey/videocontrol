package loggerForms.videocontrol;

import java.io.Serializable;

/**
 * Parameters for a single device. 
 */
public abstract class DeviceParameters implements Cloneable, Serializable, Comparable<DeviceParameters> {

	public static final long serialVersionUID = 1L;

	/**
	 * Name of the provider. Needed to set everything up when reloading. 
	 */
	public String providerName;
	
	/**
	 * @param providerName
	 */
	public DeviceParameters(String providerName) {
		super();
		this.providerName = providerName;
	}

	/**
	 * Useable name for the device - something like "Port Tracker", etc.
	 */
	public String name = "Unknown device";
	
	public int recordLengthS  = 10;

	@Override
	protected DeviceParameters clone() {
		try {
			return (DeviceParameters) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(DeviceParameters o) {
		if (name == null) {
			return 1;
		}
		return name.compareTo(o.name);
	}
	
	
}
