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
import PamView.PamSidePanel;
import loggerForms.videocontrol.protocols.AMPProvider;
import loggerForms.videocontrol.protocols.LANCProvider;
import loggerForms.videocontrol.protocols.VideoProtocol;
import loggerForms.videocontrol.protocols.VideoProtocolProvider;
import loggerForms.videocontrol.swing.dialog.VideoDialog2;
import loggerForms.videocontrol.swing.display.VideoSidePanel;

public class VideoControl extends PamControlledUnit implements PamSettings {

	public static final String unitType = "Video Control";
	
	private VideoParameters videoParameters = new VideoParameters();

	private ArrayList<VideoProtocolProvider> protocolProviders = new ArrayList<VideoProtocolProvider>();
	
	private VideoSidePanel videoSidePanel;
	
	private VideoProcess videoProcess;
	
	private ArrayList<VideoObserver> videoObservers = new ArrayList<VideoObserver>();
	
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
		
		videoProcess = new VideoProcess(this);
		addPamProcess(videoProcess);
		
		PamSettingManager.getInstance().registerSettings(this);

		setupEverything();
	}

	/**
	 * @return the videoProcess
	 */
	public VideoProcess getVideoProcess() {
		return videoProcess;
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
		VideoParameters newParams = VideoDialog2.showDialog(parentFrame, this);
		if (newParams != null) {
			videoParameters = newParams;
		}
		setupEverything();
	}
	
	private void setupEverything() {
		videoProcess.setupEverything();
		notifyConfigurationChange();
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
	public PamSidePanel getSidePanel() {
		if (videoSidePanel == null) {
			videoSidePanel = new VideoSidePanel(this);
		}
		return videoSidePanel;
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

	/**
	 * @return the videoParameters
	 */
	public VideoParameters getVideoParameters() {
		return videoParameters;
	}
	
	/**
	 * Add a video observer. 
	 * @param videoObserver
	 */
	public void addObserver(VideoObserver videoObserver) {
		videoObservers.add(videoObserver);
	}
	
	/**
	 * Remove an observer
	 * @param videoObserver
	 * @return
	 */
	public boolean removeObserver(VideoObserver videoObserver) {
		return videoObservers.remove(videoObserver);
	}
	
	/**
	 * notify a configuration change. 
	 */
	public void notifyConfigurationChange() {
		for (VideoObserver obs : videoObservers) {
			obs.configurationChange();
		}
	}
	
	/**
	 * Notify a state change of one of the recorders. 
	 * @param videoProtocol
	 */
	public void notifyStateChange(VideoProtocol videoProtocol, StatusMessage statusMessage) {
		for (VideoObserver obs : videoObservers) {
			obs.stateChange(videoProtocol, statusMessage);
		}
	}

}
