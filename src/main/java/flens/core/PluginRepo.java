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

import flens.core.Config.Option;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PluginRepo {

    private Gson gson = new Gson();
    private Map<String, String> raw = new HashMap<>();
    private Map<String, Config> processed = new HashMap<String, Config>();

    /**
     * Initialise the plugin repo, load config from all plugins.json in the
     * classpath
     */
    public PluginRepo() {
        List<URL> configs;
        try {
            configs = Collections.list(getClass().getClassLoader().getResources("plugins.json"));
            for (URL url : configs) {
                load(url.openStream());
            }
        } catch (IOException e) {
            throw new Error("IOEXception via classloader, this should NOT occur", e);
        }

    }

    /**
     * Initialise the plugin repo, load config a specific plugins.json
     * 
     * @throws FileNotFoundException
     */
    public PluginRepo(File file) throws FileNotFoundException {

        load(new FileInputStream(file));

    }

    @SuppressWarnings("unchecked")
    private void load(InputStream in) {
        Map inm = gson.fromJson(new InputStreamReader(in), HashMap.class);
        if (inm != null) {
            this.raw.putAll(inm);
        }
    }

    /**
     * Get the configurator object for the given plugin type .
     */
    public Config get(String key) {
        if (processed.containsKey(key)) {
            return processed.get(key);
        }
        String clazz = raw.get(key);

        Config out;
        try {
            out = (Config) getClass().getClassLoader().loadClass(clazz).newInstance();
            processed.put(key, out);

            return out;
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "plugin not found", e);
            return null;
        }

    }

    /**
     * Get the help string for all plugins.
     */
    public String helpString() {
        StringBuilder help = new StringBuilder();
        for (String key : raw.keySet()) {
            Config cfg = get(key);
            if (cfg != null) {
                help.append(makeHelp(key, cfg));
            }
        }
        return help.toString();
    }

    public Set<String> names() {
        return raw.keySet();
    }

    private String makeHelp(String key, Config cfg) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append(" ");
        sb.append(cfg.getDescription().replaceAll("\n", " "));
        sb.append("\n");
        for (Option opt : cfg.getOptions()) {
            sb.append("\t");
            sb.append(opt.getName());
            sb.append("\t");
            sb.append(opt.getType());
            sb.append("\t");
            sb.append(opt.getDefaultv());
            sb.append("\t");
            sb.append(opt.getDescr());
            sb.append("\n");
        }
        return sb.toString();
    }

}
