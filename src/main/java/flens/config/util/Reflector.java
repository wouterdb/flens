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

package flens.config.util;

import flens.core.Config;
import flens.core.Config.Option;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Write out plugin configuration through reflection, under the assumption that
 * each option is stored in the field with the corresponding name.
 *
 */
public class Reflector {

    /**
     * Write out plugin configuration through reflection, under the assumption
     * that each option is stored in the field with the corresponding name.
     * 
     * @param tostore
     *            plugin object to get config from
     * @param conf
     *            the assoctiated configuration object
     * 
     * @return the map containing the config
     */
    public static Map<String, Object> store(Object tostore, Config conf) {
        Map<String, Object> out = new HashMap<>();
        Map<String, Option> optmap = new HashMap<>();
        for (Option opt : conf.getOptions()) {
            optmap.put(opt.getName(), opt);
        }
        try {
            for (Field f : getFields(tostore.getClass())) {
                f.setAccessible(true);
                Object object = f.get(tostore);

                if (object instanceof ConfigWriteable) {
                    ((ConfigWriteable) object).outputConfig(out);
                } else {

                    Option opt = optmap.get(f.getName());
                    if (opt != null) {
                        assertPremissable(object);
                        out.put(f.getName(), object);
                    }
                }

            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException("bad config description", e);
        }
        return out;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void assertPremissable(Object object) {
        if (object == null) {
            return;
        }
        if (object instanceof Collection) {
            for (Object c : (Collection) object) {
                assertPremissable(c);
            }
        } else if (object instanceof Map) {
            for (Map.Entry c : ((Map<Object, Object>) object).entrySet()) {
                if (!(c.getKey() instanceof String)) {
                    throw new IllegalArgumentException("key of map is not string" + c.getKey());
                }
                assertPremissable(c.getValue());
            }
        } else {
            if (!(object instanceof Number || object instanceof String || object instanceof Boolean)) {
                throw new IllegalArgumentException("type not permissible " + object);
            }
        }

    }

    private static List<Field> getFields(Class<? extends Object> class1) {
        List<Field> acc = new LinkedList<>();
        getFields(acc, class1);
        return acc;
    }

    private static void getFields(List<Field> acc, Class<? extends Object> class1) {
        if (class1 == null) {
            return;
        }
        getFields(acc, class1.getSuperclass());

        acc.addAll(Arrays.asList(class1.getDeclaredFields()));

    }

}
