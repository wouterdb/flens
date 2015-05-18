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

import java.util.LinkedList;
import java.util.List;

public class SocketOutput extends AbstractConfig {

    @Override
    protected void construct() {
        int port = getInt("port", 19850);
        String host = get("host", "localhost");
        String field = get("field", "body");
        engine.addOutput(new flens.output.SocketOutput(name, plugin, matcher, host, port, field));
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
        out.add(new Option("port", "int", "19850", "port to connect to"));
        out.add(new Option("host", "String", "localhost", "host to connect to"));
        out.add(new Option("field", "String", "body", "field to get data from"));
        return out;
    }

    @Override
    public String getDescription() {
        return "Listen on TCP socket for opentsdb messages";
    }

}
