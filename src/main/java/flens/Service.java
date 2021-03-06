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
        
package flens;

import static flens.core.util.ConfigUtil.collectConfig;
import static flens.core.util.ConfigUtil.merge;

import flens.core.ConfigParser;
import flens.core.Util;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Service {

    /**
     * Start new flens instance, from config directory, given as the first
     * argument.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void main(String[] args) {
        try {
/*            LogManager.getLogManager().readConfiguration(
                    Service.class.getClassLoader().getResourceAsStream("logging.properties"));*/
            ConfigParser ch = new ConfigParser();

            Map<String, Object> myconfig = collectConfig(args[0]);

            final Map<String, String> tags = (Map<String, String>) myconfig.remove("tags");

            Map<String, Object> initial = (Map<String, Object>) myconfig.remove("init");

            String name = (String) myconfig.remove("name");
            if (name != null) {
                Util.overriderHostname(name);
            }

            // make mixed config work
            // init block takes precedence
            merge((Map) myconfig, (Map) initial);

            ch.construct(myconfig);

            if (tags != null) {
                ch.getEngine().addTags(tags);
            }

            if (ch.getEngine().getInputSize() == 0) {
                System.out.println("No inputs configured");
            }

            if (ch.getEngine().getOutputSize() == 0) {
                System.out.println("No outputs configured");
            }

            ch.getEngine().start();
            EmbededFlens.setInstance(ch.getEngine());
        } catch (IOException e) {
            Logger.getLogger("flens.Service").log(Level.SEVERE, "could not start Flens", e);
        }
    }

}
