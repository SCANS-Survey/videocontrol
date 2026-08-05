package loggerForms.videocontrol.swing.dialog;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import Layout.DisplayProviderList;
import PamView.dialog.PamDialogPanel;
import PamView.dialog.PamGridBagContraints;
import PamView.panel.PamAlignmentPanel;
import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.VideoControl;
import loggerForms.videocontrol.protocols.VideoProtocol;
import loggerForms.videocontrol.protocols.VideoProtocolProvider;
import loggerForms.videocontrol.protocols.amp.AMPProvider;

/**
 * Sub panel for main dialog. 
 */
public class DevicePanel extends JPanel {

	private VideoControl videoControl;
	private VideoDialog2 mainDialog;
	//	private DeviceParameters deviceParameters;

	//	/**
	//	 * @return the deviceParameters
	//	 */
	//	public DeviceParameters getDeviceParameters() {
	//		return deviceParameters;
	//	}

	private JTextField deviceName;

	private JTextField duration;

	private ArrayList<VideoProtocolProvider> protocols;

	private ProtocolDialogPanel protocolPanel;

	private JPanel protocolContainer;

	private JComboBox<VideoProtocolProvider> protocolList;

	private JButton removeButton;

	/**
	 * @param mainDialog
	 * @param videoControl
	 * @param deviceParameters
	 */
	public DevicePanel(VideoDialog2 mainDialog, VideoControl videoControl, DeviceParameters deviceParameters) {
		super();
		this.mainDialog = mainDialog;
		this.videoControl = videoControl;
		//		this.deviceParameters = deviceParameters;

		setBorder(new BevelBorder(BevelBorder.RAISED));

		protocolList = new JComboBox<VideoProtocolProvider>();
		protocols = videoControl.getProtocolProviders();
		for (int i = 0; i < protocols.size(); i++) {
			protocolList.addItem(protocols.get(i));
		}

		removeButton = new JButton("Remove");

		JPanel nPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new PamGridBagContraints();
		c.gridwidth = 2;
		nPanel.add(new JLabel("Device name"), c);
		c.gridx += c.gridwidth;
		c.gridwidth = 1;
		nPanel.add(removeButton, c);
		c.gridy++;
		c.gridx = 0;
		c.gridwidth = 3;
		nPanel.add(deviceName = new JTextField(15), c);
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy++;
		nPanel.add(new JLabel("Duration ", JLabel.RIGHT), c);
		c.gridx++;
		nPanel.add(duration = new JTextField(3), c);
		c.gridx++;
		nPanel.add(new JLabel(" seconds", JLabel.LEFT), c);
		c.gridy++;
		c.gridx = 0;
		c.gridwidth = 3;
		nPanel.add(protocolList, c);
		c.gridy++;

		protocolContainer = new JPanel(new BorderLayout());
		this.setLayout(new BorderLayout());
		this.add(BorderLayout.NORTH, nPanel);
		this.add(BorderLayout.CENTER, protocolContainer);
//		this.add(BorderLayout.SOUTH, new PamAlignmentPanel(removeButton, BorderLayout.EAST));

		setParams(deviceParameters);

		protocolList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectProtocol();
			}
		});
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeDevice();
			}
		});

		deviceName.setToolTipText("Name of device. This will be used to set up a Logger button action");
		protocolList.setToolTipText("Select camera control protocol");
		duration.setToolTipText("Recording duration in seconds");
		removeButton.setToolTipText("Remove this device");
	}

	protected void removeDevice() {
		mainDialog.removeDevice(this);
	}

	public void setParams(DeviceParameters deviceParameters) {
		VideoProtocolProvider currProtocolType = videoControl.findProvider(deviceParameters.providerName); 
		deviceName.setText(deviceParameters.name);
		duration.setText(String.format("%d", deviceParameters.recordLengthS));
		protocolContainer.removeAll();
		if (currProtocolType != null) {
			protocolList.setSelectedItem(currProtocolType);
			protocolPanel = currProtocolType.getDialogPanel(mainDialog);
			if (protocolPanel != null) {
				protocolContainer.add(BorderLayout.CENTER, protocolPanel.getDialogComponent());
				protocolPanel.setParams(deviceParameters);
			}
		}
		//		else {
		//			currentProtocol = null;
		//		}
		mainDialog.pack();
	}

	private void selectProtocol() {
		DeviceParameters currentParams = null;
		if (protocolPanel != null) {
			currentParams = protocolPanel.getParams();
		}
		if (currentParams == null) {
			currentParams = protocols.get(0).createParameters(null);
		}
		VideoProtocolProvider vpp = (VideoProtocolProvider) protocolList.getSelectedItem();
		if (vpp != null) {
			currentParams = vpp.createParameters(currentParams);
		}
		setParams(currentParams);
	}

	public DeviceParameters getParams() {
		VideoProtocolProvider vpp = (VideoProtocolProvider) protocolList.getSelectedItem();
		if (vpp == null || protocolPanel == null) {
			return null;
		}
		DeviceParameters deviceParameters = protocolPanel.getParams();
		deviceParameters.providerName = vpp.getName();
		deviceParameters.name = deviceName.getText();
		try {
			deviceParameters.recordLengthS = Integer.valueOf(duration.getText());
		}
		catch (NumberFormatException e) {
			mainDialog.showWarning("Invalid recording duration. Must be a whole number of seconds");
			return null;
		}

		return deviceParameters;
	}

}
