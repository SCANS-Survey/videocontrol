package loggerForms.videocontrol;

import java.io.Serializable;

/**
 * Parameters for a single device. 
 */
public abstract class DeviceParameters implements Cloneable, Serializable {

	public static final long serialVersionUID = 1L;

	public String name;
	
	public int recordLengthS  = 10;
}
