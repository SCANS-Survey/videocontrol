package loggerForms.videocontrol.protocols;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import PamView.dialog.PamDialogPanel;
import PamView.dialog.PamGridBagContraints;
import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.swing.dialog.ProtocolDialogPanel;

public class AMPDialogPanel implements ProtocolDialogPanel {

	private JPanel mainPanel;

	private AMPProvider ampProtocol;

	private JTextField ipAddress;

	private JTextField port;

	private AMPParameters ampParameters;

	/**
	 * @param ampProvider
	 * @param ampParameters
	 */
	public AMPDialogPanel(AMPProvider ampProvider) {
		super();
		this.ampProtocol = ampProvider;
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

//	private AMPParameters ampParameters;

	@Override
	public JComponent getDialogComponent() {
		return mainPanel;
	}

	@Override
	public void setParams(DeviceParameters deviceParameters) {
		if (deviceParameters instanceof AMPParameters == false) {
			deviceParameters = ampProtocol.createParameters(deviceParameters);
		}
		ampParameters = (AMPParameters) deviceParameters;
		ipAddress.setText(ampParameters.ipAddress);
		port.setText(String.format("%d", ampParameters.port));
	}

	@Override
	public DeviceParameters getParams() {
		ampParameters.ipAddress = ipAddress.getText();
		if (ampParameters.ipAddress == null || ampParameters.ipAddress.length() == 0) {
			return null;
		}
		try {
			ampParameters.port = Integer.valueOf(port.getText());
		}
		catch (NumberFormatException e) {
			return null;
		}
		return ampParameters;
	}

}
