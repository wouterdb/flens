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

package flens;

import flens.core.ConfigBuilder;
import flens.core.Flengine;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Embedded instance of flens.
 * <p/>
 * It attempt to read a config from
 * <ul>
 * <li>the filename found in the system property flens.config</li>
 * <li>the file called flens.json, as found by the classloader</li>
 * </ul>
 *
 */
public class EmbededFlens {

    /**
     * Engine used by this EmbededFlens instance.
     */
    private Flengine engine;

    protected EmbededFlens() {
        Reader config = getConfig();
        if (config == null) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                    "EmbededFlens could not find any configuration, bailing out.");
            return;
        }
        ConfigBuilder cb = new ConfigBuilder(config);
        cb.run();
        this.engine = cb.getEngine();
    }

    public EmbededFlens(Flengine engine) {
        this.engine = engine;
    }

    protected Reader getConfig() {
        String prop = System.getProperty("flens.config");
        if (prop == null) {
            return getHardConfig();
        }
        try {
            return new FileReader(prop);
        } catch (FileNotFoundException e) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Config file not found", e);
            return getHardConfig();
        }
    }

    protected Reader getHardConfig() {
        return new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("flens.json"));
    }

    public Flengine getEngine() {
        return engine;
    }

    private static EmbededFlens instance;

    /**
     * Only works correctly if a configuration is present.
     * 
     * @return the shared EmbededFlens instance for this machine.
     */
    public static synchronized EmbededFlens getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new EmbededFlens();
        if (instance.engine != null) {
            instance.engine.start();
        }
        return instance;
    }

    public static synchronized void setInstance(Flengine engine) {
        instance = new EmbededFlens(engine);
    }

}
