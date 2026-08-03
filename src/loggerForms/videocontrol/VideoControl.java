package loggerForms.videocontrol;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JMenuItem;

import PamController.PamConfiguration;
import PamController.PamControlledUnit;
import PamController.PamControlledUnitSettings;
import PamController.PamSettingManager;
import PamController.PamSettings;
import loggerForms.videocontrol.swing.VideoDialog;

public class VideoControl extends PamControlledUnit implements PamSettings {

	public static final String unitType = "Video Control";
	
	private VideoParameters videoParameters = new VideoParameters();

	public VideoControl(String unitName) {
		this(null, unitName);
	}
	
	public VideoControl(PamConfiguration pamConfiguration, String unitName) {
		super(pamConfiguration, unitType, unitName);
		
		PamSettingManager.getInstance().registerSettings(this);
	}

	@Override
	public JMenuItem createDetectionMenu(Frame parentFrame) {
		JMenuItem menuItem = new JMenuItem(getUnitName() + " settings ...");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showSettingsDialog(parentFrame);
			}
		});
		return menuItem;
	}

	protected void showSettingsDialog(Frame parentFrame) {
		VideoParameters newParams = VideoDialog.showDialog(parentFrame, this, videoParameters);
	}

	@Override
	public Serializable getSettingsReference() {
		return videoParameters;
	}

	@Override
	public long getSettingsVersion() {
		return VideoParameters.serialVersionUID;
	}

	@Override
	public boolean restoreSettings(PamControlledUnitSettings pamControlledUnitSettings) {
		this.videoParameters = (VideoParameters) pamControlledUnitSettings.getSettings();
		return true;
	}
	
	

}
