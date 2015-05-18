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
        
package flens.core;

import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public class ConfigParser {

    private Flengine engine;
    // TODO use GSON to parse with costum objectbuilder
    protected Gson gson = new Gson();
    private PluginRepo pluginRepo;

    /**
     * Start new engine and load plugins.
     */
    public ConfigParser() {
        loadPlugins();
        this.engine = new Flengine(pluginRepo);
    }

    /**
     * Start parser for existing engine.
     */
    public ConfigParser(Flengine engine) {
        this.engine = engine;
        loadPlugins();
    }

    public Flengine getEngine() {
        return engine;
    }

    protected void load(Map<String, Object> map) {
        if (map == null) {
            return;
        }

        SortedMap<Integer, List<String>> keys = new TreeMap<>();

        for (Map.Entry<String, Object> entry : ((Map<String, Object>) map).entrySet()) {
            String key = (String) entry.getKey();

            Map<String, Object> value = (Map<String, Object>) entry.getValue();

            Integer prion = 5;

            if (value.containsKey("prio")) {
                Object prio = value.get("prio");
                if (prio instanceof String) {
                    prion = Integer.parseInt((String) prio);
                } else {
                    prion = ((Number) prio).intValue();
                }
            }
            if (!keys.containsKey(prion)) {
                keys.put(prion, new LinkedList<String>());
            }
            keys.get(prion).add(key);

        }

        for (List<String> xentry : keys.values()) {
            for (String entry : xentry) {
                Map<String, Object> child = (Map<String, Object>) map.get(entry);
                String plugin = (String) child.get("plugin");
                if (plugin == null) {
                    plugin = (String) entry;
                }

                Config config = pluginRepo.get(plugin);
                if (config == null) {
                    Logger.getLogger("flens").severe("plugin not found: " + entry);
                } else {
                    config.readConfigPart((String) entry, child, engine);
                }
            }
        }

    }

    protected void unload(Map<?, ?> map) {
        if (map == null) {
            return;
        }

        for (Object entry : map.keySet()) {

            String name = (String) entry;

            engine.remove(name);
        }

    }

    private void loadPlugins() {

        this.pluginRepo = new PluginRepo();
    }

    /**
     * Load in the config from map.
     * The following keys are used:
     * <ul>
     * <li>input</li>
     * <li>filter</li>
     * <li>output</li>
     * <li>query</li>
     * </ul>
     */
    public void construct(Map<String, Object> config) {
        load((Map<String, Object>) config.remove("query"));
        load((Map<String, Object>) config.remove("input"));
        load((Map<String, Object>) config.remove("output"));
        load((Map<String, Object>) config.remove("filter"));
    }

    public String help() {
        return pluginRepo.helpString();
    }
}
