package loggerForms.videocontrol.protocols.canon;

import javax.swing.JComponent;
import javax.swing.JPanel;

import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.swing.dialog.ProtocolDialogPanel;

public class CanonDialogPanel implements ProtocolDialogPanel {

	private JPanel mainPanel;
	private CanonProvider canonProvider;
	
	/**
	 * @param canonProtocol
	 */
	public CanonDialogPanel(CanonProvider canonProvider) {
		super();
		this.canonProvider = canonProvider;
		mainPanel = new JPanel();
	}

	@Override
	public JComponent getDialogComponent() {
		return mainPanel;
	}

	@Override
	public void setParams(DeviceParameters deviceParameters) {
		// TODO Auto-generated method stub

	}

	@Override
	public DeviceParameters getParams() {
		// TODO Auto-generated method stub
		return null;
	}

}
