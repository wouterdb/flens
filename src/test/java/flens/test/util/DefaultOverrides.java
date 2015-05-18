/*
 *
 *     Copyright 2013-2015 KU Leuven Research and Development - iMinds - Distrinet
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     Administrative Contact: dnet-project-office@cs.kuleuven.be
 *     Technical Contact: wouter.deborger@cs.kuleuven.be
 */
        
package flens.test.util;

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
        specialOverrides.put("log-type-check.dir", "src/test/resources/logtypes");
       
        specialOverrides.put("geo-ip.database", "src/test/resources/GeoLite2-City.mmdb");
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
