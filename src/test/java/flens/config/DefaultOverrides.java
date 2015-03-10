package flens.config;

import flens.core.Config.Option;

import java.util.HashMap;
import java.util.Map;

public class DefaultOverrides {

    private static Map<String, String> specialOverrides = new HashMap<>();

    static {
        specialOverrides.put("cookbook.template", "dummy.tmpl");
        specialOverrides.put("grep.file", "/etc/hosts");
        specialOverrides.put("http-poll.url", "http://www.google.be/");
        specialOverrides.put("metric-type-check.dir", "src/test/resources/types");
    }

    public static String getDefaultFor(String pluginName, Option option) {
        if (specialOverrides.containsKey(pluginName + "." + option.getName())) {
            return specialOverrides.get(pluginName + "." + option.getName());
        }
        String def = option.getDefaultv();
        if (def != null && !def.isEmpty()) {
            return def;
        }

        if (option.getName().equals("matches")) {
            return "";
        }

        return getDefaultFor(option.getType());
    }

    private static String getDefaultFor(String type) {
        if ("String".equals(type)) {
            return "UNKNOWN";
        }
        if ("[String]".equals(type)) {
            return "[UNKNOWN,UNKNOWN]";
        }
        throw new IllegalArgumentException(type);
    }

}
