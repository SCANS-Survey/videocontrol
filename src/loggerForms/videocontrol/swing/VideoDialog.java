package loggerForms.videocontrol.swing;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import PamView.dialog.PamDialog;
import PamView.dialog.PamDialogPanel;
import PamView.dialog.PamGridBagContraints;
import PamView.panel.PamAlignmentPanel;
import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.VideoControl;
import loggerForms.videocontrol.VideoParameters;
import loggerForms.videocontrol.protocols.VideoProtocol;
import loggerForms.videocontrol.protocols.VideoProtocolProvider;

public class VideoDialog extends PamDialog {

	private static VideoDialog singleInstance;

	private VideoParameters videoParameters;

	private JPanel mainPanel;

	private JPanel devicePanel;

	private JComboBox<String> deviceList;

	private JButton addButton, removeButton;

	private TitledBorder deviceBorder;

	private DeviceParameters currentDevice;

	private JComboBox<VideoProtocolProvider> protocolList;

	private VideoControl videoControl;

	private ArrayList<VideoProtocolProvider> protocols;

	private VideoProtocol currentProtocol;

	private PamDialogPanel currentProtocolPanel;

	private VideoDialog(Window parentFrame, VideoControl videoControl) {
		/**
		 * Top panel has a drop down to select which device to edit, and an add button. 
		 * bottom panel has details for selected provider and a remove button. 
		 */
		super(parentFrame, videoControl.getUnitName(), false);
		this.videoControl = videoControl;
		mainPanel = new JPanel(new BorderLayout());
		devicePanel = new JPanel(new BorderLayout());
		JPanel southPanel = new JPanel(new BorderLayout());
		JPanel northPanel = new JPanel(new BorderLayout());
		mainPanel.add(BorderLayout.NORTH, northPanel);
		mainPanel.add(BorderLayout.CENTER, southPanel);
		JPanel baseProt = new JPanel(new GridBagLayout());

		southPanel.add(BorderLayout.CENTER, devicePanel);
		southPanel.setBorder(deviceBorder = new TitledBorder("Protocol"));
		northPanel.setBorder(new TitledBorder("Camera / Device"));

		protocolList = new JComboBox<VideoProtocolProvider>();
		deviceList = new JComboBox<String>();
		addButton = new JButton("Add");
		removeButton = new JButton("Remove");

		deviceList.setEditable(true);

		GridBagConstraints c = new PamGridBagContraints();
		c.gridwidth = 2;
//		baseProt.add(new JLabel("Protocol"), c);
//		c.gridy++;
		baseProt.add(protocolList, c);
		c.gridy++;

		northPanel.add(BorderLayout.CENTER, deviceList);
		northPanel.add(BorderLayout.EAST, addButton);
		southPanel.add(BorderLayout.NORTH, new PamAlignmentPanel(baseProt, BorderLayout.WEST));
		southPanel.add(BorderLayout.SOUTH, new PamAlignmentPanel(removeButton, BorderLayout.EAST));

		deviceList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectDevice();
			}
		});
		protocolList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectProtocol();
			}
		});
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addDevice();
			}
		});
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeDevice();
			}
		});

		protocols = videoControl.getProtocolProviders();
		for (int i = 0; i < protocols.size(); i++) {
			protocolList.addItem(protocols.get(i));
		}

		setDialogComponent(mainPanel);
	}

	protected void selectDevice() {
		// save existing one. 
		getParams();
		// load up the new one. 
		if (videoParameters == null) {
			return;
		}
		String devName = (String) deviceList.getSelectedItem();
		DeviceParameters dp = videoParameters.getDeviceParameters(devName);
		if (dp == null) {
			dp = protocols.get(0).getProtocol(videoControl).getDeviceParameters();
		}
		VideoProtocolProvider vpp = videoControl.findProvider(dp.providerName);
		if (vpp == null) {
			return; // this can't happen !
		}
		protocolList.setSelectedItem(vpp);
		selectProtocol();
	}

	protected void selectProtocol() {
		if (videoParameters == null) {
			return;
		}
		VideoProtocolProvider vpp = (VideoProtocolProvider) protocolList.getSelectedItem();
		VideoProtocol vp = vpp.getProtocol(videoControl);
		currentProtocolPanel = null;
		devicePanel.removeAll();
		if (vpp != null) {
			PamDialogPanel newPan = vp.getDialogPanel(this);
			if (newPan != null) {
				currentProtocolPanel = newPan;
				devicePanel.add(BorderLayout.CENTER, newPan.getDialogComponent());
			}
		}
		String devName = (String) deviceList.getSelectedItem();
		DeviceParameters dp = videoParameters.getDeviceParameters(devName);
		if (dp != null) {
			vp.setDeviceParameters(dp);
		}
		if (currentProtocolPanel != null) {
			currentProtocolPanel.setParams();
		}
		currentProtocol = vp;
		pack();
	}

	protected void addDevice() {
		VideoProtocolProvider firstProt = protocols.get(0);
		VideoProtocol newProt = firstProt.getProtocol(videoControl);
		videoParameters.setDeviceParameters(newProt.getDeviceParameters());
		setParams(videoParameters);
	}

	protected void removeDevice() {

	}

	private void setParams(VideoParameters videoParameters) {
		this.videoParameters = videoParameters;
		TreeMap<String, DeviceParameters> devices = videoParameters.getDeviceParameters();
		Set<String> keys = devices.keySet();
		deviceList.removeAllItems();
		for (String key : keys) {
			deviceList.addItem(devices.get(key).toString());
		}
		if (currentDevice != null) {
			deviceList.setSelectedItem(currentDevice);
		}

		selectDevice();
	}

	@Override
	public boolean getParams() {
		String deviceName = (String) deviceList.getSelectedItem();
		return getParams(deviceName);
	}
	
	private boolean getParams(String deviceName) {
		if (currentProtocolPanel == null) {
			return false;
		}
		boolean ok = currentProtocolPanel.getParams();
		String name = (String) deviceList.getSelectedItem();
		DeviceParameters params = currentProtocol.getDeviceParameters();
		params.name = name;
		
		return ok;
	}

	@Override
	public void cancelButtonPressed() {
		videoParameters = null;
	}

	@Override
	public void restoreDefaultSettings() {
		// TODO Auto-generated method stub

	}

	public static VideoParameters showDialog(Frame parentFrame, VideoControl videoControl,
			VideoParameters videoParameters) {
		singleInstance = new VideoDialog(parentFrame, videoControl);
		singleInstance.setParams(videoParameters);
		singleInstance.setVisible(true);
		return singleInstance.videoParameters;
	}

}
