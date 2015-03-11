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

package flens.config;

import flens.config.util.AbstractConfig;
import flens.core.Config;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GeoIPFilter extends AbstractConfig {
    protected boolean isIn() {
        return false;
    }

    @SuppressWarnings("unchecked")
    protected void construct() {
        String from = get("from-field", "source");
        String to = get("to-field", "geoip");
        String db = get("database", "");

        try {
            this.engine.addFilter(new flens.filter.GeoIPFilter(this.name, plugin, this.tagger, this.matcher, prio,
                    from, to, db));
        } catch (IOException e) {
            warn("GeoIP plugin not started", e);
        }
    }

    protected boolean isOut() {
        return false;
    }

    public String getDescription() {
        return "find location for a given IP or hostname";
    }

    /**
     * @see flens.config.util.AbstractConfig#getOptions()
     */
    public List<Config.Option> getOptions() {
        LinkedList<Option> out = new LinkedList<Option>(super.getOptions());
        out.add(new Config.Option("from-field", "String", "source", "name of field to take input from"));
        out.add(new Config.Option("to-field", "String", "geoip", "name of the field to put output into"));
        out.add(new Config.Option("database", "String", "", "file containing a GeoIP2 city database"));

        return out;
    }
}