package loggerForms.videocontrol;

import java.util.ArrayList;

import PamguardMVC.PamProcess;
import loggerForms.videocontrol.protocols.VideoProtocol;
import loggerForms.videocontrol.protocols.VideoProtocolProvider;

public class VideoProcess extends PamProcess {

	private VideoControl videoControl;
	
	private ArrayList<VideoProtocol> runningProtocols = new ArrayList<VideoProtocol>();

	public VideoProcess(VideoControl videoControl) {
		super(videoControl, null);
		this.videoControl = videoControl;
	}

	public void setupEverything() {
		stopEverything();
		VideoParameters params = videoControl.getVideoParameters();
		ArrayList<DeviceParameters> devices = params.getDeviceParameters();
		for (int i = 0; i < devices.size(); i++) {
			DeviceParameters deviceParameters = devices.get(i);
			VideoProtocolProvider provider = videoControl.findProvider(deviceParameters.providerName);
			if (provider == null) {
				System.err.println("Unknown video control protocol: " + deviceParameters.providerName);
				continue;
			}
			VideoProtocol protocol = provider.getProtocol(videoControl, deviceParameters);
			runningProtocols.add(protocol);
			protocol.connect();
		}
	}
	
	public void stopEverything() {
		for (VideoProtocol p : runningProtocols) {
			p.stopRecording();
			p.disconnect();
		}
		runningProtocols.clear();
	}

	/**
	 * @return the runningProtocols
	 */
	public ArrayList<VideoProtocol> getRunningProtocols() {
		return runningProtocols;
	}

	@Override
	public void pamStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pamStop() {
		// TODO Auto-generated method stub

	}

}
