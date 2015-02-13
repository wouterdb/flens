/*
 *
 *     Copyright 2013 KU Leuven Research and Development - iMinds - Distrinet
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

package flens.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MapParserUtil {

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getSubtree(Map<String, Object> tree, String name) {
        Object object = tree.remove(name);
        if (object == null) {
            return Collections.emptyMap();
        }
        if (object instanceof Map) {
            return (Map<String, Object>) object;
        }

        throw new IllegalArgumentException("expected map, got " + tree);
    }

    @SuppressWarnings("rawtypes")
    public static List getArray(Map<String, Object> tree, String name, List<?> defaultv) {
        Object object = tree.remove(name);
        if (object == null) {
            return defaultv;
        }
        if (object instanceof List) {
            return (List) object;
        }

        return Collections.singletonList(object);

        // throw new IllegalArgumentException("not a list: " + o);

    }

    public static String get(Map<String, Object> tree, String name, String defaultv) {
        String res = (String) tree.remove(name);
        if (res == null) {
            return defaultv;
        }
        return res;
    }

    public static boolean getBool(Map<String, Object> tree, String namex, boolean defaultv) {
        Object res = tree.remove(namex);

        if (res == null) {
            return defaultv;
        }
        if (res instanceof Boolean) {
            return ((Boolean) res).booleanValue();
        }

        return Boolean.parseBoolean((String) res);
    }

    public static int getInt(Map<String, Object> tree, String namex, int defaultv) {
        Object res = tree.remove(namex);

        if (res == null) {
            return defaultv;
        }
        if (res instanceof Number) {
            return ((Number) res).intValue();
        }

        return Integer.parseInt((String) res);
    }

    public static void done(Map<String, Object> in) {
        if (!in.isEmpty()) {
            warn("unknown values {0}", in);
        }
        
    }

    protected static void warn(String string, Object... args) {
        Logger.getLogger("MapParseUtil").log(Level.WARNING, string, args);
    }
}
