package loggerForms.videocontrol.protocols;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import PamView.dialog.PamDialogPanel;
import PamView.dialog.PamGridBagContraints;

public class AMPDialogPanel implements PamDialogPanel {

	private JPanel mainPanel;

	private AMPProtocol ampProtocol;

	private JTextField ipAddress;

	private JTextField port;

	/**
	 * @param ampProtocol
	 * @param ampParameters
	 */
	public AMPDialogPanel(AMPProtocol ampProtocol, AMPParameters ampParameters) {
		super();
		this.ampProtocol = ampProtocol;
		this.ampParameters = ampParameters;
		mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new PamGridBagContraints();
		mainPanel.add(new JLabel("ip address", JLabel.RIGHT), c);
		c.gridx++;
		mainPanel.add(ipAddress = new JTextField(15));
		c.gridx = 0;
		c.gridy++;
		mainPanel.add(new JLabel("port", JLabel.RIGHT), c);
		c.gridx++;
		c.fill = GridBagConstraints.NONE;
		mainPanel.add(port = new JTextField(5), c);
	}

	private AMPParameters ampParameters;

	@Override
	public JComponent getDialogComponent() {
		return mainPanel;
	}

	@Override
	public void setParams() {
		ipAddress.setText(ampParameters.ipAddress);
		port.setText(String.format("%d", ampParameters.port));
	}

	@Override
	public boolean getParams() {
		ampParameters.ipAddress = ipAddress.getText();
		if (ampParameters.ipAddress == null || ampParameters.ipAddress.length() == 0) {
			return false;
		}
		try {
			ampParameters.port = Integer.valueOf(port.getText());
		}
		catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

}
