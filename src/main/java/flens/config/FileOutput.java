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

import java.util.LinkedList;
import java.util.List;

public class FileOutput extends AbstractConfig {

    @Override
    protected void construct() {
        String file = get("file", "/tmp/flenslog");
        String field = get("field", "body");
        boolean newline = getBool("newline", false);
        engine.addOutput(new flens.output.FileOutput(name, plugin, matcher, file, field, newline));
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
        out.add(new Option("file", "String", "/tmp/flenslog", "file to write to"));
        out.add(new Option("newline", "Boolean", "false", "add newline to messages?"));
        out.add(new Option("field", "String", "body", "field to get data from"));
        return out;
    }

    @Override
    public String getDescription() {
        return "write to file";
    }

}
