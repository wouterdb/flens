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

public class GrokFilter extends AbstractConfig {

    @Override
    protected boolean isIn() {
        return false;
    }

    @Override
    protected void construct() {
        String script = get("script", "");
        String inf = get("infield", "");
        String dir = get("dir", "");

        boolean discard = getBool("discard", false);
        engine.addFilter(new flens.filter.GrokFilter(name, plugin, tagger, matcher, prio, script, inf, dir, discard));

    }

    @Override
    protected boolean isOut() {
        return false;
    }

    @Override
    public String getDescription() {
        return "run grok on a field";
    }

    @Override
    public List<Option> getOptions() {
        List<Option> out = new LinkedList<Option>(super.getOptions());
        out.add(new Option("script", "String", "", "script to execute"));
        out.add(new Option("infield", "String", "", "field to match"));
        out.add(new Option("dir", "String", "", "directory for aux definitions"));
        out.add(new Option("discard", "boolean", "false", "discard if no match"));
        return out;
    }
}
