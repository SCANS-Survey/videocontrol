package loggerForms.videocontrol.swing.display;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import PamView.PamSidePanel;
import PamView.panel.PamPanel;
import loggerForms.videocontrol.StatusMessage;
import loggerForms.videocontrol.VideoControl;
import loggerForms.videocontrol.VideoObserver;
import loggerForms.videocontrol.VideoProcess;
import loggerForms.videocontrol.protocols.VideoProtocol;

public class VideoSidePanel implements PamSidePanel, VideoObserver {

	private VideoControl videoControl;
	
	private JPanel mainPanel;
	
	private JPanel devicesPanel;

	private VideoProcess videoProcess;

	public VideoSidePanel(VideoControl videoControl) {
		this.videoControl = videoControl;
		this.videoProcess = videoControl.getVideoProcess();
		
		mainPanel = new PamPanel();
//		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		rename(videoControl.getUnitName());
		devicesPanel = new PamPanel();
		mainPanel.add(devicesPanel);		
		BoxLayout boxLayout;
		devicesPanel.setLayout(boxLayout = new BoxLayout(devicesPanel, BoxLayout.Y_AXIS));
//		boxLayout.
		
		configurationChange();
		
		videoControl.addObserver(this);
		
	}

	@Override
	public JComponent getPanel() {
		return mainPanel;
	}

	@Override
	public void rename(String newName) {
		mainPanel.setBorder(new TitledBorder(newName));

	}

	@Override
	public void configurationChange() {
		createDevicePanels();
	}

	private void createDevicePanels() {
		destroyDevicePanels();
		ArrayList<VideoProtocol> currentProtocols = videoProcess.getRunningProtocols();
		for (VideoProtocol vp : currentProtocols) {
			DeviceSideStrip dp = new DeviceSideStrip(videoControl, vp);
			devicesPanel.add(dp);
		}
	}
	
	private void destroyDevicePanels() {
		int n = devicesPanel.getComponentCount();
		for (int i = 0; i < n; i++) {
			Component comp = devicesPanel.getComponent(i);
			if (comp instanceof DeviceSideStrip) {
				DeviceSideStrip ds = (DeviceSideStrip) comp;
				videoControl.removeObserver(ds);
			}
		}
		devicesPanel.removeAll();
	}
	
	/**
	 * Find side strip for a video protocol
	 * @param videoProtocol
	 * @return
	 */
	private DeviceSideStrip findSideStrip(VideoProtocol videoProtocol) {
		int n = devicesPanel.getComponentCount();
		for (int i = 0; i < n; i++) {
			Component comp = devicesPanel.getComponent(i);
			if (comp instanceof DeviceSideStrip) {
				DeviceSideStrip ds = (DeviceSideStrip) comp;
				if (ds.getVideoProtocol() == videoProtocol) {
					return ds;
				}
			}
		}
		return null;
	}

	@Override
	public void stateChange(VideoProtocol videoProtocol, StatusMessage statusMessage) {
		DeviceSideStrip sideStrip = findSideStrip(videoProtocol);
		if (sideStrip != null) {
			sideStrip.stateChange(videoProtocol, statusMessage);
		}
	}

}
