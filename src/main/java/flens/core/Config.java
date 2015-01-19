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

package flens.core;

import java.util.List;
import java.util.Map;

public interface Config {

    public static class Option {
        private String name;
        private String type;
        private String descr;
        private String defaultv;

        /**
         * Construct a new Option.
         */
        public Option(String name, String type, String defaultv, String descr) {
            super();
            this.name = name;
            this.type = type;
            this.descr = descr;
            this.defaultv = defaultv;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getDescr() {
            return descr;
        }

        public String getDefaultv() {
            return defaultv;
        }

    }

    public void readConfigPart(String plugin, Map<String, Object> tree, Flengine engine);

    public List<Option> getOptions();

    public String getDescription();

}
