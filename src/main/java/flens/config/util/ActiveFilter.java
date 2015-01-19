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

package flens.config.util;

import flens.core.Config;
import flens.core.Flengine;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class ActiveFilter extends AbstractConfig {

    protected boolean isIn() {
        return true;
    }

    protected boolean isOut() {
        return false;
    }

    protected boolean isQuery() {
        return false;
    }

    protected boolean isFilter() {
        return false;
    }

    @Override
    public void readConfigPart(String name, Map<String, Object> tree, Flengine engine) {
        this.tree = tree;
        this.engine = engine;
        this.name = name;
        this.plugin = get("plugin", name);

        logger.info("starting: " + name);

        matcher = readMatcher();
        tagger = readTagger("out-");

        construct();

        if (!tree.isEmpty()) {
            warn("unknown values {0}", tree);
        }
    }

    @Override
    public List<Option> getOptions() {

        List<Option> matcherOpts = new LinkedList<Config.Option>();
        matcherOpts.add(new Option("plugin", "String", "plugin name",
                "name of the filter, for reporting and monitoring purposes"));

        matcherOpts.add(new Option("type", "String", "name", "only apply to records having this type"));
        matcherOpts.add(new Option("tags", "[String]", "[]", "only apply to records having all of these tags"));

        matcherOpts.add(new Option("out-type", "String", "name", "type to apply to the records"));
        matcherOpts.add(new Option("out-add-tags", "[String]", "[]", "add following tags"));

        return matcherOpts;
    }

}
