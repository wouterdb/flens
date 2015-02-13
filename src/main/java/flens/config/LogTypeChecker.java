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
import flens.core.Tagger;
import flens.typing.LogTypesDb;

import java.util.LinkedList;
import java.util.List;

public class LogTypeChecker extends AbstractConfig {
    @Override
    protected boolean isIn() {
        return false;
    }

    @Override
    protected boolean isOut() {
        return false;
    }

    @Override
    protected void construct() {
       
        Tagger unknown = readTagger("unknown-");
        String dir = get("dir", "");
        boolean refresh = getBool("refresh", false);
        boolean breakOnMatch = getBool("breakOnMatch", true);

        if (!refresh) {
            if (dir.isEmpty()) {
                throw new UnsupportedOperationException();
            } else {
                engine.addFilter(new flens.filter.LogTypeChecker(name, plugin, matcher, prio, tagger,
                        unknown, new LogTypesDb(dir),dir,refresh,breakOnMatch));
            }

        } else {
            throw new UnsupportedOperationException();
        }

    }

    @Override
    public String getDescription() {
        return "expand log types according to a library of grok rules";
    }

    @Override
    public List<Option> getOptions() {
        List<Option> out = new LinkedList<Option>(super.getOptions());
        out.add(new Option("unknown-add-tags", "[String]", "[]", "add following tags to records with unknown type"));
        out.add(new Option("unknown-remove-tags", "[String]", "[]",
                "add following tags from records with unknown type"));
        out.add(new Option("unknown-set-type", "String", "", "add following type to records with unknown type"));
        
        
        out.add(new Option("dir", "String", "", "directory to from which to read .db files"));
        out.add(new Option("refresh", "boolean", "false", "scan for file updates continuously"));
        out.add(new Option("breakOnMatch", "boolean", "true", "break after a matching pattern is found"));
        return out;
    }
}
