package loggerForms.videocontrol.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import PamView.dialog.PamDialog;
import loggerForms.videocontrol.DeviceParameters;
import loggerForms.videocontrol.VideoControl;
import loggerForms.videocontrol.VideoParameters;
import loggerForms.videocontrol.protocols.amp.AMPParameters;
import loggerForms.videocontrol.protocols.amp.AMPProvider;

public class VideoDialog2 extends PamDialog {

	private static VideoDialog2 singleinstance;
	private VideoParameters videoParameters;
	
	private JButton addButton;
	private JPanel mainPanel;
	private JPanel deviceContainer;
	private VideoControl videoControl;
	
	private VideoDialog2(Window parentFrame, VideoControl videoControl) {
		super(parentFrame, videoControl.getUnitName(), false);
		this.videoControl = videoControl;
		
		addButton = new JButton("Add device");
		
		mainPanel = new JPanel(new BorderLayout());
		JPanel nPanel = new JPanel(new BorderLayout());
		mainPanel.add(BorderLayout.NORTH, nPanel);
		nPanel.add(BorderLayout.EAST, addButton);
		deviceContainer = new JPanel();
//		deviceContainer.setLayout(new BoxLayout(deviceContainer, BoxLayout.Y_AXIS));
		deviceContainer.setLayout(new GridLayout(0, 2));
		mainPanel.add(BorderLayout.CENTER, deviceContainer);
		
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addDevice(new AMPParameters(AMPProvider.providerName));
			}
		});
		
		setDialogComponent(mainPanel);
	}
	
	public static VideoParameters showDialog(Window parent, VideoControl videoControl) {
		singleinstance = new VideoDialog2(parent, videoControl);
		singleinstance.setParams(videoControl.getVideoParameters());
		singleinstance.setVisible(true);
		
		return singleinstance.videoParameters;
	}

	private void setParams(VideoParameters videoParameters) {
		this.videoParameters = videoParameters;
		ArrayList<DeviceParameters> devices = videoParameters.getDeviceParameters();
		for (DeviceParameters dp : devices) {
			addDevice(dp);
		}
	}

	private void addDevice(DeviceParameters dp) {
		DevicePanel panel = new DevicePanel(singleinstance, videoControl, dp);
		deviceContainer.add(panel);
		pack();
	}

	@Override
	public boolean getParams() {
		int n = deviceContainer.getComponentCount();
		ArrayList<DeviceParameters> deviceList = new ArrayList<DeviceParameters>();
		for (int i = 0; i < n; i++) {
			Component component = deviceContainer.getComponent(i);
			if (component instanceof DevicePanel == false) {
				continue; 
			}
			DevicePanel dp = (DevicePanel) component;
			DeviceParameters dParams = dp.getParams();
			if (dParams == null) {
				return showWarning("Error in device parameters for device " + i+1);
			}
			deviceList.add(dParams);
		}
		ArrayList<DeviceParameters> mainList = videoParameters.getDeviceParameters();
		mainList.clear();
		Collections.sort(deviceList);
		mainList.addAll(deviceList);
		return true;
	}

	@Override
	public void cancelButtonPressed() {
		this.videoParameters = null;
	}

	@Override
	public void restoreDefaultSettings() {
		// TODO Auto-generated method stub

	}

	public void removeDevice(DevicePanel devicePanel) {
		this.deviceContainer.remove(devicePanel);
		pack();
	}

}
