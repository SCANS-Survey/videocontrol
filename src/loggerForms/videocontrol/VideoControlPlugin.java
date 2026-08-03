package loggerForms.videocontrol;

import PamModel.PamDependency;
import PamModel.PamModel;
import PamModel.PamPluginInterface;

public class VideoControlPlugin implements PamPluginInterface {

	private String jarFile;
	
	private static String defName = "Video Device Control";
	
	@Override
	public String getDefaultName() {
		return defName;
	}

	@Override
	public String getHelpSetName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setJarFile(String jarFile) {
		this.jarFile = jarFile;
	}

	@Override
	public String getJarFile() {
		return jarFile;
	}

	@Override
	public String getDeveloperName() {
		return "Doug Gillespie";
	}

	@Override
	public String getContactEmail() {
		return "pamguard@pamguard.org";
	}

	@Override
	public String getVersion() {
		return "0.0";
	}

	@Override
	public String getPamVerDevelopedOn() {
		return "2.02.19";
	}

	@Override
	public String getPamVerTestedOn() {
		return "2.02.19";
	}

	@Override
	public String getAboutText() {
		return "Control of video cameras using AMP, LANC, and other protocols";
	}

	@Override
	public String getClassName() {
		return VideoControl.class.getName();
	}

	@Override
	public String getDescription() {
		return defName;
	}

	@Override
	public String getMenuGroup() {
		return PamModel._VisualGroup;
	}

	@Override
	public String getToolTip() {
		return "Control of video cameras and capture devices from Logger forms";
	}

	@Override
	public PamDependency getDependency() {
		return null;
	}

	@Override
	public int getMinNumber() {
		return 0;
	}

	@Override
	public int getMaxNumber() {
		return 0;
	}

	@Override
	public int getNInstances() {
		return 0;
	}

	@Override
	public boolean isItHidden() {
		return false;
	}

	@Override
	public int allowedModes() {
		return 0;
	}

}
