package loggerForms.videocontrol.protocols.lumix;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Java port of the python_lumix_control.CameraControl class (https://github.com/palmdalian/python_lumix_control).
 * Provides basic HTTP-based control calls for Panasonic Lumix cameras that expose a /cam.cgi interface.
 *
 * This class mirrors the functions in the original Python file and is intended to be used by
 * the LumixProtocol implementation in this repository.
 */
public class LumixCameraControl {
	private final String camIp;
	private final String baseurl;
	private final HttpClient client;

	public LumixCameraControl(String camIp) {
		this.camIp = camIp;
		this.baseurl = "http://" + camIp + "/cam.cgi";
		this.client = HttpClient.newHttpClient();
		startCameraControl();
	}

	private String sendGet(Map<String, String> params) {
		try {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> e : params.entrySet()) {
				if (sb.length() > 0) sb.append('&');
				sb.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8))
					.append('=')
					.append(URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8));
			}
			String uri = baseurl + "?" + sb.toString();
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(uri))
				.GET()
				.build();
			HttpResponse<String> resp = client.send(request, BodyHandlers.ofString());
			return resp.body();
		} catch (IOException | InterruptedException ex) {
			Thread.currentThread().interrupt();
			System.err.println("Error sending request: " + ex.getMessage());
			return null;
		}
	}

	public void startCameraControl() {
		Map<String, String> params = new HashMap<>();
		params.put("mode", "camcmd");
		params.put("value", "recmode");
		String resp = sendGet(params);
		if (checkResponse(resp)) {
			System.out.println("Connected");
		}
	}

	public boolean startStream(int udpPort) {
		Map<String, String> params = new HashMap<>();
		params.put("mode", "startstream");
		params.put("value", Integer.toString(udpPort));
		String resp = sendGet(params);
		return checkResponse(resp);
	}

	public boolean stopStream() {
		Map<String, String> params = new HashMap<>();
		params.put("mode", "stopstream");
		String resp = sendGet(params);
		return checkResponse(resp);
	}

	public String getInfo(String setting) {
		Map<String, String> params = new HashMap<>();
		params.put("mode", "getinfo");
		params.put("type", setting);
		return sendGet(params);
	}

	public String currentMenuInfo() {
		return getInfo("curmenu");
	}

	public String allMenuInfo() {
		return getInfo("allmenu");
	}

	public String getLensInfo() {
		return getInfo("lens");
	}

	public String getSetting(String setting) {
		Map<String, String> params = new HashMap<>();
		params.put("mode", "getsetting");
		params.put("type", setting);
		return sendGet(params);
	}

	public String getFocusMode() {
		return getSetting("focusmode");
	}

	public String getFocusMag() {
		return getSetting("mf_asst_mag");
	}

	public String getMfAsstSetting() {
		return getSetting("mf_asst");
	}

	public String setSetting(Map<String, String> settings) {
		Map<String, String> params = new HashMap<>();
		params.put("mode", "setsetting");
		params.putAll(settings);
		return sendGet(params);
	}

	public void setIso(String ISO) {
		if ("auto".equalsIgnoreCase(ISO)) {
			ISO = "50";
		}
		Map<String, String> s = new HashMap<>();
		s.put("type", "iso");
		s.put("value", ISO);
		String resp = setSetting(s);
		if (checkResponse(resp)) {
			System.out.println("ISO set to " + ISO);
		}
	}

	public void setFocal(String focal) {
		Map<String, String> fstop = new HashMap<>();
		fstop.put("1", "0/256");
		fstop.put("1.1", "85/256");
		fstop.put("1.2", "171/256");
		fstop.put("1.4", "256/256");
		fstop.put("1.6", "341/256");
		fstop.put("1.8", "427/256");
		fstop.put("2", "512/256");
		fstop.put("2.2", "597/256");
		fstop.put("2.4", "640/256");
		fstop.put("2.8", "768/256");
		fstop.put("3.2", "853/256");
		fstop.put("3.5", "939/256");
		fstop.put("4", "1024/256");
		fstop.put("4.5", "1110/256");
		fstop.put("5", "1195/256");
		fstop.put("5.6", "1280/256");
		fstop.put("6.3", "1364/256");
		fstop.put("7.1", "1451/256");
		fstop.put("8", "1536/256");
		fstop.put("9", "1621/256");
		fstop.put("10", "1707/256");
		fstop.put("11", "1792/256");
		fstop.put("13", "1877/256");
		fstop.put("14", "1963/256");
		fstop.put("16", "2048/256");
		fstop.put("18", "2133/256");
		fstop.put("20", "2219/256");
		fstop.put("22", "2304/256");

		if (!fstop.containsKey(focal)) {
			System.err.println("Unknown focal value: " + focal);
			return;
		}
		Map<String, String> s = new HashMap<>();
		s.put("type", "focal");
		s.put("value", fstop.get(focal));
		String resp = setSetting(s);
		if (checkResponse(resp)) {
			System.out.println("F Stop set to " + focal);
		}
	}

	public void setShutter(String shutter) {
		Map<String, String> shutterSpeed = new HashMap<>();
		shutterSpeed.put("4000", "3072/256");
		shutterSpeed.put("3200", "2987/256");
		shutterSpeed.put("2500", "2902/256");
		shutterSpeed.put("2000", "2816/256");
		shutterSpeed.put("1600", "2731/256");
		shutterSpeed.put("1300", "2646/256");
		shutterSpeed.put("1000", "2560/256");
		shutterSpeed.put("800", "2475/256");
		shutterSpeed.put("640", "2390/256");
		shutterSpeed.put("500", "2304/256");
		shutterSpeed.put("400", "2219/256");
		shutterSpeed.put("320", "2134/256");
		shutterSpeed.put("250", "2048/256");
		shutterSpeed.put("200", "1963/256");
		shutterSpeed.put("160", "1878/256");
		shutterSpeed.put("125", "1792/256");
		shutterSpeed.put("100", "1707/256");
		shutterSpeed.put("80", "1622/256");
		shutterSpeed.put("60", "1536/256");
		shutterSpeed.put("50", "1451/256");
		shutterSpeed.put("40", "1366/256");
		shutterSpeed.put("30", "1280/256");
		shutterSpeed.put("25", "1195/256");
		shutterSpeed.put("20", "1110/256");
		shutterSpeed.put("15", "1024/256");
		shutterSpeed.put("13", "939/256");
		shutterSpeed.put("10", "854/256");
		shutterSpeed.put("8", "768/256");
		shutterSpeed.put("6", "683/256");
		shutterSpeed.put("5", "598/256");
		shutterSpeed.put("4", "512/256");
		shutterSpeed.put("3.2", "427/256");
		shutterSpeed.put("2.5", "342/256");
		shutterSpeed.put("2", "256/256");
		shutterSpeed.put("1.6", "171/256");
		shutterSpeed.put("1.3", "86/256");
		shutterSpeed.put("1", "0/256");
		shutterSpeed.put("1.3s", "-85/256");
		shutterSpeed.put("1.6s", "-170/256");
		shutterSpeed.put("2s", "-256/256");
		shutterSpeed.put("2.5s", "-341/256");
		shutterSpeed.put("3.2s", "-426/256");
		shutterSpeed.put("4s", "-512/256");
		shutterSpeed.put("5s", "-682/256");
		shutterSpeed.put("6s", "-768/256");
		shutterSpeed.put("8s", "-853/256");
		shutterSpeed.put("10s", "-938/256");
		shutterSpeed.put("13s", "-1024/256");
		shutterSpeed.put("15s", "-1109/256");
		shutterSpeed.put("20s", "-1194/256");
		shutterSpeed.put("25s", "-1280/256");
		shutterSpeed.put("30s", "-1365/256");
		shutterSpeed.put("40s", "-1450/256");
		shutterSpeed.put("50s", "-1536/256");
		shutterSpeed.put("60s", "16384/256");
		shutterSpeed.put("B", "256/256");

		if (!shutterSpeed.containsKey(shutter)) {
			System.err.println("Unknown shutter value: " + shutter);
			return;
		}
		Map<String, String> s = new HashMap<>();
		s.put("type", "shtrspeed");
		s.put("value", shutterSpeed.get(shutter));
		String resp = setSetting(s);
		if (checkResponse(resp)) {
			System.out.println("Shutter set to " + shutter);
		}
	}

	public String setVideoQuality(String quality) {
		if (quality == null) {
			quality = "mp4ed_30p_100mbps_4k";
		}
		Map<String, String> s = new HashMap<>();
		s.put("type", "videoquality");
		s.put("value", quality);
		String resp = setSetting(s);
		if (checkResponse(resp)) {
			System.out.println("Video quality set to " + quality);
		}
		return resp;
	}

	public String focusControl(String direction, String speed) {
		Map<String, String> params = new HashMap<>();
		params.put("mode", "camctrl");
		params.put("type", "focus");
		params.put("value", direction + "-" + speed);
		return sendGet(params);
	}

	/**
	 * Attempts to perform a rack focus. This mirrors the behavior of the python implementation but is best-effort:
	 * it repeatedly issues focus control commands and polls the returned position. The exact behavior depends on
	 * camera responses and the network.
	 */
	public void rackFocus(String startPoint, String endPoint, String speed) {
		try {
			String resp = focusControl("tele", "normal");
			if (resp == null) return;
			int currentPosition = parsePositionFromFocusResponse(resp);

			if ("current".equals(endPoint)) {
				endPoint = Integer.toString(currentPosition + 13);
			}

			if ("current".equals(startPoint)) {
				startPoint = Integer.toString(currentPosition + 13);
			} else {
				// ensure startPoint numeric
			}

			int start = Integer.parseInt(startPoint);
			int end = Integer.parseInt(endPoint);

			int threshold = "fast".equals(speed) ? 70 : 13;

			// Move to start point if needed
			if (start < currentPosition) {
				while (currentPosition - start > 13) {
					resp = focusControl("tele", "fast");
					Thread.sleep(100);
					currentPosition = parsePositionFromFocusResponse(resp);
				}
			} else if (start > currentPosition) {
				while (start - currentPosition > 13) {
					resp = focusControl("wide", "fast");
					Thread.sleep(100);
					currentPosition = parsePositionFromFocusResponse(resp);
				}
			}

			// Now move to end point
			if (start > end) {
				while (currentPosition - end > threshold) {
					resp = focusControl("tele", speed);
					Thread.sleep(100);
					currentPosition = parsePositionFromFocusResponse(resp);
					if (currentPosition - end <= threshold) {
						threshold = 13;
						speed = "normal";
					}
				}
			} else {
				while (end - currentPosition > threshold) {
					resp = focusControl("wide", speed);
					Thread.sleep(100);
					currentPosition = parsePositionFromFocusResponse(resp);
					if (end - currentPosition <= threshold) {
						threshold = 13;
						speed = "normal";
					}
				}
			}
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			System.err.println("rackFocus interrupted");
		}
	}

	private int parsePositionFromFocusResponse(String resp) {
		if (resp == null) return 0;
		try {
			String[] parts = resp.split(",");
			if (parts.length >= 2) {
				return Integer.parseInt(parts[1].trim());
			}
		} catch (NumberFormatException ex) {
			// fall through
		}
		return 0;
	}

	public String capturePhoto() {
		Map<String, String> params = new HashMap<>();
		params.put("mode", "camcmd");
		params.put("value", "capture");
		return sendGet(params);
	}

	public String videoRecordStart() {
		Map<String, String> params = new HashMap<>();
		params.put("mode", "camcmd");
		params.put("value", "video_recstart");
		return sendGet(params);
	}

	public String videoRecordStop() {
		Map<String, String> params = new HashMap<>();
		params.put("mode", "camcmd");
		params.put("value", "video_recstop");
		return sendGet(params);
	}

	public boolean checkResponse(String resp) {
		if (resp != null && resp.contains("<result>ok</result>")) {
			return true;
		} else {
			if (resp != null) System.err.println(resp);
			return false;
		}
	}
}
