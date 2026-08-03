package loggerForms.videocontrol;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JMenuItem;

import PamController.PamConfiguration;
import PamController.PamControlledUnit;
import PamController.PamControlledUnitSettings;
import PamController.PamSettingManager;
import PamController.PamSettings;
import loggerForms.videocontrol.protocols.AMPProvider;
import loggerForms.videocontrol.protocols.LANCProvider;
import loggerForms.videocontrol.protocols.VideoProtocolProvider;
import loggerForms.videocontrol.swing.VideoDialog;

public class VideoControl extends PamControlledUnit implements PamSettings {

	public static final String unitType = "Video Control";
	
	private VideoParameters videoParameters = new VideoParameters();

	private ArrayList<VideoProtocolProvider> protocolProviders = new ArrayList<VideoProtocolProvider>();
	
	/**
	 * @return the protocolProviders
	 */
	public ArrayList<VideoProtocolProvider> getProtocolProviders() {
		return protocolProviders;
	}

	public VideoControl(String unitName) {
		this(null, unitName);
	}
	
	public VideoControl(PamConfiguration pamConfiguration, String unitName) {
		super(pamConfiguration, unitType, unitName);
		
		protocolProviders.add(new AMPProvider());
		protocolProviders.add(new LANCProvider());
		
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
	
	/**
	 * Find a provider by name
	 * @param providerName
	 * @return
	 */
	public VideoProtocolProvider findProvider(String providerName) {
		for (VideoProtocolProvider vpp : protocolProviders) {
			if (vpp.getName().equals(providerName)) {
				return vpp;
			}
		}
		return null;
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
