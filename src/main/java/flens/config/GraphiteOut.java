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
package flens.config;

import flens.config.util.AbstractConfig;
import flens.output.GraphiteOutput;

import java.util.LinkedList;
import java.util.List;

public class GraphiteOut extends AbstractConfig {

    private static final String DEFAULT_METRIC = 
            "@{reverseHostname(source)}.@{metric}@{(isdef instance)?'.'+instance:''}";

    @Override
    protected boolean isIn() {
        return false;
    }

    @Override
    protected void construct() {
        String host = get("host", "localhost");
        int port = getInt("port", 2003);
        String template = get("metric", DEFAULT_METRIC);
        engine.addOutput(new GraphiteOutput(name, plugin, matcher, host, port, template));
    }

    @Override
    protected boolean isOut() {
        return true;
    }

    @Override
    public List<Option> getOptions() {
        List<Option> out = new LinkedList<Option>(super.getOptions());
        out.add(new Option("port", "int", "4242", "port to which to connect"));
        out.add(new Option("host", "String", "localhost", "host to which to connect"));
        out.add(new Option("metric", "String", DEFAULT_METRIC, "mvel template to use as metric name for graphite"));
        return out;
    }

    @Override
    public String getDescription() {
        return "send out records to an graphite server \n"
                + "send out messages of the form \n ${metric} ${value} ${timestamp/1000}";
    }

}
