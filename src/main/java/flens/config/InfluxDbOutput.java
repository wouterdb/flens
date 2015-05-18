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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class InfluxDbOutput extends AbstractConfig {

    @SuppressWarnings("unchecked")
    @Override
    protected void construct() {
        String host = get("host", "localhost");
        int port = getInt("port", 8086);
        String db = get("db", "metrics");
        String user = get("user", "admin");
        String pass = get("pass", "guest");

        String metricname = get("metric", "@{metric}");
        String[] collname = { "time", "source", "value", "instance" };
        String[] colltemplate = { "@{time}", "@{source}", "@{value}", "@{(isdef instance)?instance:null}" };
        List<String> collnames = getArray("fieldnames", Arrays.asList(collname));
        List<String> colltemplates = getArray("fieldTemplates", Arrays.asList(colltemplate));
        engine.addOutput(new flens.output.InFluxOutput(name, plugin, matcher, host, port, db, user, pass, metricname,
                collnames, colltemplates));
    }

    @Override
    protected boolean isIn() {
        return false;
    }

    @Override
    protected boolean isOut() {
        return true;
    }

    @Override
    public List<Option> getOptions() {
        List<Option> out = new LinkedList<Option>(super.getOptions());

        out.add(new Option("host", "String", "localhost", "host to connect to"));
        out.add(new Option("port", "int", "4369", "port to connect to"));
        out.add(new Option("db", "String", null, "database used"));
        out.add(new Option("user", "String", "guest", "username"));
        out.add(new Option("pass", "String", "guest", "password"));
        out.add(new Option("metric", "String", "@{metric}", "mvel template to construct metric name"));
        out.add(new Option("fieldnames", "[String]", "['time','source','value','instance']",
                "list of names of fields to send to influx"));
        out.add(new Option("fieldTemplates", "[String]",
                "'@{time}','@{source}','@{value}','@{(isdef instance)?instance:null}'",
                "list of mvel template of to create values for fields"));
        return out;
    }

    @Override
    public String getDescription() {
        return "Send Influxdb messages to an influx server";
    }

}
