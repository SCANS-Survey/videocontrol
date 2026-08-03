package loggerForms.videocontrol.swing;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.JPanel;

import PamView.dialog.PamDialog;
import loggerForms.videocontrol.VideoControl;
import loggerForms.videocontrol.VideoParameters;

public class VideoDialog extends PamDialog {

	private static VideoDialog singleInstance;
	
	private VideoParameters videoParameters;
	
	private JPanel mainPanel;
	
	private JPanel devicePanel;
	
	private VideoDialog(Window parentFrame, VideoControl videoControl) {
		super(parentFrame, videoControl.getUnitName(), false);
		mainPanel = new JPanel(new BorderLayout());
		devicePanel = new JPanel();
		mainPanel.add(BorderLayout.SOUTH, devicePanel);
		
		
		setDialogComponent(mainPanel);
	}

	@Override
	public boolean getParams() {
		// TODO Auto-generated method stub
		return false;
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

	private void setParams(VideoParameters videoParameters) {
		this.videoParameters = videoParameters;
	}

}
