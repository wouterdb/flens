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
import flens.output.GraphiteOutput;
import flens.output.StatsdOutput;

import java.util.LinkedList;
import java.util.List;

public class StatsdOut extends AbstractConfig {

    private static final String DEFAULT_METRIC = "";

    @Override
    protected boolean isIn() {
        return false;
    }

    @Override
    protected void construct() {
        String host = get("host", "localhost");
        int port = getInt("port", 8125);
        String template = get("metric", DEFAULT_METRIC);
        if (template.isEmpty()) {
            System.out.println("no template give to statsd" + name);
        } else {
            engine.addOutput(new StatsdOutput(name, plugin, matcher, host, port, template));
        }
    }

    @Override
    protected boolean isOut() {
        return true;
    }

    @Override
    public List<Option> getOptions() {
        List<Option> out = new LinkedList<Option>(super.getOptions());
        out.add(new Option("port", "int", "8125", "port to which to connect"));
        out.add(new Option("host", "String", "localhost", "host to which to connect"));
        out.add(new Option("metric", "String", DEFAULT_METRIC, "mvel template to construct the metric to send to statsd"));
        return out;
    }

    @Override
    public String getDescription() {
        return "send out records to a stasd server";
    }

}
