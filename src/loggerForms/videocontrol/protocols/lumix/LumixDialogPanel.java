package loggerForms.videocontrol.protocols.lumix;

import javax.swing.JComponent;
import javax.swing.JPanel;

import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.swing.dialog.ProtocolDialogPanel;

public class LumixDialogPanel implements ProtocolDialogPanel {
	
	private JPanel mainPanel;

	public LumixDialogPanel(LumixProvider lumixProvider) {
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
