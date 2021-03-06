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
import flens.core.Config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RenameFilter extends AbstractConfig {
    protected boolean isIn() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void construct() {
        List<String> from = getArray("from", Collections.EMPTY_LIST);
        List<String> to = getArray("to", Collections.EMPTY_LIST);
        if (from.size() != to.size()) {
            throw new IllegalArgumentException("to and from must be same size");
        }
        this.engine.addFilter(
                new flens.filter.RenameFilter(this.name, plugin, this.tagger, this.matcher, prio, from, to));
    }

    protected boolean isOut() {
        return false;
    }

    public String getDescription() {
        return "rrename fields";
    }

    /**
     * @see flens.config.util.AbstractConfig#getOptions()
     */
    public List<Config.Option> getOptions() {
        LinkedList<Option> out = new LinkedList<Option>(super.getOptions());
        out.add(new Config.Option("from", "List", "[]", "field names to change"));
        out.add(new Config.Option("to", "List", "[]", "names to change to"));
        return out;
    }
}