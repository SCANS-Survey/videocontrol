package loggerForms.videocontrol.protocols;

import javax.swing.JComboBox;

/**
 * Simple combo box that offers the different shoot modes. 
 */
public class ShootModeComboBox extends JComboBox<ShootMode> {

	private static final long serialVersionUID = 1L;


	public ShootModeComboBox() {
		super(ShootMode.values());
	}

	/**
	 * Set selected shoot mode
	 * @param shootMode
	 */
	public void setShootMode(ShootMode shootMode) {
		this.setSelectedItem(shootMode);
	}
	
	/**
	 * Get selected shoot mode. 
	 * @return
	 */
	public ShootMode getShootMode() {
		return (ShootMode) this.getSelectedItem();
	}
}
