package loggerForms.videocontrol.protocols.canon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;

/**
 * Port of CCAPI code at https://github.com/laszewsk/canon-r7-ccapi/blob/main/ccapi/ccapi.py
 * translated from Python to Java.
 * <br>
 * API function outline is at <br>
 * https://developercommunity.usa.canon.com/s/article/CCAPI-Function-List
 */
public class CCAPI {

    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    private String ip;
    private int port;
    private boolean debug;
    private String settingsFile;
    private ObjectNode settings; // stores combined ver110 / ver100 JSON

    public CCAPI() throws IOException, InterruptedException {
        this(null, 8080, true, "canon-settings.json");
    }

    public CCAPI(String ip, int port, boolean debug, String settingsFile) throws IOException, InterruptedException {
        this.httpClient = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
        this.debug = debug;
        this.settingsFile = settingsFile;
        if (ip == null || ip.isEmpty()) {
            String env = System.getenv("CANON_IP");
            if (env != null && !env.isEmpty()) {
                this.ip = env;
            } else {
                this.ip = "192.168.50.210"; // fallback default used in Python code
            }
        } else {
            this.ip = ip;
        }
        this.port = port;
        this.settings = mapper.createObjectNode();
        getSettings(true); // populate settings on construction
    }

    /* ---------- HTTP helpers ---------- */

    private HttpResponse<String> _getRaw(String path) throws IOException, InterruptedException {
        String url = String.format("http://%s:%d%s", ip, port, path);
        if (debug) System.out.println("GET: " + url);
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> _putRaw(String path, JsonNode json) throws IOException, InterruptedException {
        String url = String.format("http://%s:%d%s", ip, port, path);
        if (debug) System.out.println("PUT: " + url + " <- " + (json == null ? "null" : json.toString()));
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(url)).PUT(HttpRequest.BodyPublishers.ofString(json == null ? "" : json.toString()));
        b.header("Content-Type", "application/json");
        return httpClient.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> _postRaw(String path, JsonNode json) throws IOException, InterruptedException {
        String url = String.format("http://%s:%d%s", ip, port, path);
        if (debug) System.out.println("POST: " + url + " <- " + (json == null ? "null" : json.toString()));
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(url)).POST(HttpRequest.BodyPublishers.ofString(json == null ? "" : json.toString()));
        b.header("Content-Type", "application/json");
        return httpClient.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }

    private JsonNode _get(String path) throws IOException, InterruptedException {
        HttpResponse<String> r = _getRaw(path);
        return mapper.readTree(r.body());
    }

    private JsonNode _put(String path, JsonNode json) throws IOException, InterruptedException {
        HttpResponse<String> r = _putRaw(path, json);
        return mapper.readTree(r.body());
    }

    private JsonNode _post(String path, JsonNode json) throws IOException, InterruptedException {
        HttpResponse<String> r = _postRaw(path, json);
        return mapper.readTree(r.body());
    }

    /* ---------- Device info / status ---------- */

    public JsonNode getDeviceInformation() throws IOException, InterruptedException {
        return _get("/ccapi/ver100/deviceinformation");
    }

    public JsonNode getTemperature() throws IOException, InterruptedException {
        return _get("/ccapi/ver100/devicestatus/temperature");
    }

    public String getTemperatureStatus() throws IOException, InterruptedException {
        JsonNode n = getTemperature();
        return n.has("status") ? n.get("status").asText() : null;
    }

    public ZonedDateTime getDateTime() throws IOException, InterruptedException {
        JsonNode n = _get("/ccapi/ver100/functions/datetime");
        String dt = n.has("datetime") ? n.get("datetime").asText() : null;
        if (dt == null) return null;
        // The Python sliced off last 6 chars; try parsing RFC_1123_DATE_TIME
        try {
            return ZonedDateTime.parse(dt, DateTimeFormatter.RFC_1123_DATE_TIME);
        } catch (Exception ex) {
            // try removing timezone like Python did
            String p = dt.length() > 6 ? dt.substring(0, dt.length() - 6) : dt;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss");
            return ZonedDateTime.parse(p, fmt.withZone(java.time.ZoneId.systemDefault()));
        }
    }

    /* ---------- Settings ---------- */

    public ObjectNode getSettings(boolean refresh) throws IOException, InterruptedException {
        Path settingsPath = Paths.get(settingsFile);
        if (!refresh && Files.exists(settingsPath)) {
            try (InputStream in = Files.newInputStream(settingsPath)) {
                JsonNode loaded = mapper.readTree(in);
                if (loaded.isObject()) {
                    settings = (ObjectNode) loaded;
                    return settings;
                }
            }
        }

        // otherwise fetch from camera
        ObjectNode combined = mapper.createObjectNode();
        try {
            JsonNode ver110 = _get("/ccapi/ver110/shooting/settings");
            JsonNode ver100 = _get("/ccapi/ver100/shooting/settings");
            combined.set("ver110", ver110);
            combined.set("ver100", ver100);

            // add "api" helper fields similar to Python version
            Iterator<Map.Entry<String, JsonNode>> versions = combined.fields();
            while (versions.hasNext()) {
                Map.Entry<String, JsonNode> verEntry = versions.next();
                String verName = verEntry.getKey();
                JsonNode verNode = verEntry.getValue();
                if (verNode != null && verNode.isObject()) {
                    Iterator<String> keys = verNode.fieldNames();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        JsonNode entry = verNode.get(key);
                        if (entry != null && entry.isObject()) {
                            ObjectNode entryObj = (ObjectNode) entry;
                            // set api path like /shooting/settings/<key-with-slash>
                            String api = "/shooting/settings/" + key.replace("_", "/");
                            entryObj.put("api", api);
                            // best-effort kind detection (same heuristic as Python)
                            try {
                                if (key.equals("stillimagequality") || key.equals("wbshift") || key.contains("picturestyle")) {
                                    entryObj.put("kind", "unknown");
                                } else if (entryObj.has("ability") && entryObj.get("ability").toString().contains("min")) {
                                    entryObj.put("kind", "slider");
                                } else {
                                    entryObj.put("kind", "choice");
                                }
                            } catch (Exception ex) {
                                // ignore
                            }
                        }
                    }
                }
            }

            // save to file
            try (OutputStream out = Files.newOutputStream(settingsPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(out, combined);
            }

            this.settings = combined;
            return combined;
        } catch (IOException | InterruptedException ex) {
            if (Files.exists(settingsPath)) {
                // fallback to disk content on error
                try (InputStream in = Files.newInputStream(settingsPath)) {
                    JsonNode loaded = mapper.readTree(in);
                    if (loaded.isObject()) {
                        settings = (ObjectNode) loaded;
                        return settings;
                    }
                }
            }
            throw ex;
        }
    }

    public JsonNode getSettingsValue(String key) throws IOException, InterruptedException {
        String version = getSettingsVersion(key);
        return _get(String.format("/ccapi/%s/shooting/settings/%s", version, key));
    }

    public String getSettingsVersion(String key) {
        if (settings == null) return "ver110";
        if (settings.has("ver110") && settings.get("ver110").has(key)) return "ver110";
        if (settings.has("ver100") && settings.get("ver100").has(key)) return "ver100";
        return "ver110";
    }

    public JsonNode setSettingsValue(String key, JsonNode valueNode) throws IOException, InterruptedException {
        String version = getSettingsVersion(key);
        JsonNode old = null;
        try {
            old = getSettingsValue(key);
        } catch (Exception ignored) {
        }
        ObjectNode payload = mapper.createObjectNode();
        payload.set("value", valueNode);
        return _put(String.format("/ccapi/%s/shooting/settings/%s", version, key), payload);
    }

    /* ---------- Storage / contents ---------- */

    public JsonNode getStorage() throws IOException, InterruptedException {
        JsonNode r = _get("/ccapi/ver110/devicestatus/storage");
        if (r.has("storagelist")) return r.get("storagelist");
        return r;
    }

    public JsonNode contents() throws IOException, InterruptedException {
        // similar to Python's contents(): list files on cards
        JsonNode root = _get("/ccapi/ver110/contents");
        if (!root.has("path")) return root;
        ArrayNodeWrapper images = new ArrayNodeWrapper(mapper);
        for (JsonNode pathNode : root.get("path")) {
            String path = pathNode.asText();
            JsonNode directories = _get(path).get("path");
            if (directories == null) continue;
            for (JsonNode d : directories) {
                String dStr = d.asText();
                int pages = _get(dStr + "?kind=number").get("pagenumber").asInt();
                for (int page = 1; page <= pages; page++) {
                    JsonNode files = _get(dStr + "?page=" + page).get("path");
                    for (JsonNode f : files) {
                        images.add(mapper.convertValue(f.asText(), JsonNode.class));
                    }
                }
            }
        }
        return images.asArrayNode();
    }

    /* ---------- Controls ---------- */

    private String _getEnable(Object on) {
        if (on == null) return "disable";
        String s = on.toString().toLowerCase();
        if (s.equals("1") || s.equals("on") || s.equals("true") || s.equals("enable")) {
            return "enable";
        }
        return "disable";
    }

    private boolean _getBool(Object on) {
        if (on == null) return false;
        String s = on.toString().toLowerCase();
        return s.equals("1") || s.equals("on") || s.equals("true");
    }

    public JsonNode autofocus(Object on) throws IOException, InterruptedException {
        boolean action = _getBool(on);
        String act = action ? "start" : "stop";
        ObjectNode payload = mapper.createObjectNode();
        payload.put("action", act);
        return _post("/ccapi/ver100/shooting/control/af", payload);
    }

    public JsonNode flickerDetection(Object on) throws IOException, InterruptedException {
        boolean action = _getBool(on);
        String act = action ? "start" : "stop";
        ObjectNode payload = mapper.createObjectNode();
        payload.put("action", act);
        return _post("/ccapi/ver100/shooting/control/flickerdetection", payload);
    }

    public JsonNode shoot(Object af) throws IOException, InterruptedException {
        boolean afBool = _getBool(af);
        ObjectNode payload = mapper.createObjectNode();
        payload.put("af", afBool);
        return _post("/ccapi/ver100/shooting/control/shutterbutton", payload);
    }

    public JsonNode shootControl(Object af, String action) throws IOException, InterruptedException {
        boolean afBool = _getBool(af);
        if (!action.equals("full_press") && !action.equals("half_press") && !action.equals("release")) {
            throw new IllegalArgumentException("Invalid action: " + action);
        }
        ObjectNode payload = mapper.createObjectNode();
        payload.put("af", afBool);
        payload.put("action", action);
        return _post("/ccapi/ver100/shooting/control/shutterbutton/manual", payload);
    }

    public JsonNode liveview(String display, String size) throws IOException, InterruptedException {
        if (!display.equals("on") && !display.equals("off") && !display.equals("keep")) {
            throw new IllegalArgumentException("Invalid display");
        }
        if (!size.equals("small") && !size.equals("off") && !size.equals("medium")) {
            throw new IllegalArgumentException("Invalid size");
        }
        ObjectNode payload = mapper.createObjectNode();
        payload.put("cameradisplay", display);
        payload.put("liveviewsize", size);
        return _post("/ccapi/ver100/shooting/liveview", payload);
    }

    /* ---------- Simple convenience getters ---------- */

    public int getCharge() throws IOException, InterruptedException {
        JsonNode n = _get("/ccapi/ver110/devicestatus/batterylist");
        if (n.has("batterylist") && n.get("batterylist").isArray() && n.get("batterylist").size() > 0) {
            JsonNode b = n.get("batterylist").get(0);
            if (b.has("level")) {
                return b.get("level").asInt();
            }
        }
        return -1;
    }

    /* ---------- Download helpers ---------- */

    public void download(String cameraPath, Path toFile, boolean refresh) throws IOException, InterruptedException {
        // cameraPath is expected to start with /ccapi/... or a full URL
        String url;
        if (cameraPath.startsWith("http")) {
            url = cameraPath;
        } else {
            url = String.format("http://%s:%d%s", ip, port, cameraPath);
        }
        if (debug) System.out.println("Downloading: " + url);
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (!Files.exists(toFile) || refresh) {
            try (InputStream in = response.body()) {
                Files.copy(in, toFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    /* ---------- Utility: beep ---------- */

    public static void beep() {
        System.out.print("\u0007");
        System.out.flush();
    }

    /* ---------- Example main ---------- */

    public static void main(String[] args) {
        try {
            CCAPI camera = new CCAPI(null, 8080, true, "canon-settings.json");
            JsonNode device = camera.getDeviceInformation();
            System.out.println("Device: " + device.toPrettyString());

            // example: call liveview on
            JsonNode live = camera.liveview("on", "medium");
            System.out.println("Liveview result: " + live);

            // beep and charge
            beep();
            System.out.println("Charge: " + camera.getCharge());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ---------- Small helper class to build array nodes easily ---------- */
    private static class ArrayNodeWrapper {
        private final ObjectMapper mapper;
        private final com.fasterxml.jackson.databind.node.ArrayNode arr;

        ArrayNodeWrapper(ObjectMapper mapper) {
            this.mapper = mapper;
            this.arr = mapper.createArrayNode();
        }

        void add(JsonNode n) {
            arr.add(n);
        }

        com.fasterxml.jackson.databind.node.ArrayNode asArrayNode() {
            return arr;
        }
    }
}