package loggerForms.videocontrol.protocols;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import PamView.dialog.PamGridBagContraints;
import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.swing.dialog.ProtocolDialogPanel;
import serialComms.jserialcomm.PJSerialComm;

public class LANCDialogPanel implements ProtocolDialogPanel {

	private JPanel mainPanel;

	private LANCProvider lancProtocol;

	private LANCParameters lancParams;

	private JComboBox<String> portComboBox = new JComboBox<String>();

	private JComboBox<Integer> bitsPerSecondComboBox = new JComboBox<Integer>();	

	public int[] bitsPerSecondList = {110, 300, 1200, 2400, 4800, 9600, 
			19200, 38400, 57600, 115200, 230400, 460800, 921600};

	/**
	 * @param lancProtocol
	 */
	public LANCDialogPanel(LANCProvider lancProtocol) {
		super();
		this.lancProtocol = lancProtocol;
		mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new PamGridBagContraints();

		mainPanel.add(new JLabel("Port", JLabel.RIGHT), c);
		c.gridx++;
		mainPanel.add(portComboBox, c);
		c.gridx = 0;
		c.gridy++;
		mainPanel.add(new JLabel("BAUD", JLabel.RIGHT), c);
		c.gridx++;
		mainPanel.add(bitsPerSecondComboBox, c);
	}

	//	private 

	@Override
	public JComponent getDialogComponent() {
		return mainPanel;
	}

	@Override
	public void setParams(DeviceParameters deviceParameters) {
		if (deviceParameters instanceof LANCParameters) {
			lancParams = (LANCParameters) deviceParameters;
		}
		else {
			lancParams = lancProtocol.createParameters(deviceParameters);
		}
		populateComboBoxes();
		portComboBox.setSelectedItem(lancParams.port);
		bitsPerSecondComboBox.setSelectedItem(lancParams.bitRate);
	}

	private void populateComboBoxes(){
		portComboBox.removeAllItems();
//		ArrayList<CommPortIdentifier> portIds = SerialPortCom.getPortArrayList();
		String[] portIds = PJSerialComm.getSerialPortNames();
		for(int i = 0; i<portIds.length; i++){
			portComboBox.addItem(portIds[i]);
		}
		
		bitsPerSecondComboBox.removeAllItems();
		for(int i = 0; i<bitsPerSecondList.length; i++){
			bitsPerSecondComboBox.addItem(bitsPerSecondList[i]);
		}
		
	}

	@Override
	public DeviceParameters getParams() {
		if (lancParams == null) {
			lancParams = lancProtocol.createParameters(null);
		}
		lancParams.port = (String) portComboBox.getSelectedItem();
		if (lancParams.port == null) {
			return null;
		}
		lancParams.bitRate = (Integer) bitsPerSecondComboBox.getSelectedItem();
		
		return lancParams;
	}


}
