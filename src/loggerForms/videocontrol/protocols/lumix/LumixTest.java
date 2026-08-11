package loggerForms.videocontrol.protocols.lumix;

public class LumixTest {

	private static final String ip = "192.168.0.101";
	public static void main(String[] args) {
		new LumixTest().runTest();
	}
	
	private void runTest() {
		LumixCameraControl lcc = new LumixCameraControl(ip);
		Boolean ok;
		ok = lcc.startCameraControl();
		System.out.println(ok);
		saycmd("Info", lcc.getSetting("device_name"));
		
		System.out.println(lcc.getLensInfo());
		saycmd("Start", lcc.capturePhoto());
//		System.out.println(lcc.videoRecordStart());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		saycmd("Second start", lcc.capturePhoto());
	 saycmd("Send stop",	lcc.stopCapture());
	}
	
	private void saycmd(String cmd, String result) {
		if (result != null) {
			result = result.replace("\r\n", "");
		}
		System.out.printf("%s: returned %s\n", cmd, result);
	}

}
