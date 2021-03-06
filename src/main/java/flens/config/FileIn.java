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



public class FileIn extends AbstractConfig {

    @Override
    protected boolean isIn() {
        return true;
    }

    @Override
    protected void construct() {
        String file = get("file", null);
        String regex = get("regex", ".*");
        boolean tail = getBool("tail", true);
        engine.addInput(new flens.input.FileInput(name, plugin, tagger, file, regex, tail));
    }

    @Override
    protected boolean isOut() {
        return false;
    }

    @Override
    public List<Option> getOptions() {
        List<Option> out = new LinkedList<Option>(super.getOptions());
        out.add(new Option("file", "String", "", "file to tail"));
        out.add(new Option("regex", "String", ".*", "regex to filter results"));
        out.add(new Option("tail", "Boolean", "true", "tail from the end of the file?"));
        return out;
    }

    @Override
    public String getDescription() {
        return "log tailer with regex support";
    }

}
