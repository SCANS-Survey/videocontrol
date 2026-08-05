package loggerForms.videocontrol.protocols.lumix;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import PamView.dialog.PamGridBagContraints;
import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.protocols.amp.AMPParameters;
import loggerForms.videocontrol.swing.dialog.ProtocolDialogPanel;

public class LumixDialogPanel implements ProtocolDialogPanel {
	
	private JPanel mainPanel;
	
	private JTextField ipAddress;

	private LumixProvider lumixProvider;

	private LumixParameters lumixParameters;

	public LumixDialogPanel(LumixProvider lumixProvider) {
		this.lumixProvider = lumixProvider;
		mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new PamGridBagContraints();
		mainPanel.add(new JLabel("ip address", JLabel.RIGHT), c);
		c.gridx++;
		mainPanel.add(ipAddress = new JTextField(15));
		
	}

	@Override
	public JComponent getDialogComponent() {
		return mainPanel;
	}

	@Override
	public void setParams(DeviceParameters deviceParameters) {
		if (deviceParameters instanceof LumixParameters == false) {
			deviceParameters = lumixProvider.createParameters(deviceParameters);
		}
		lumixParameters = (LumixParameters) deviceParameters; 
		ipAddress.setText(lumixParameters.ipAddress);
	}

	@Override
	public DeviceParameters getParams() {
		lumixParameters.ipAddress = ipAddress.getText();
		return lumixParameters;
	}

}
