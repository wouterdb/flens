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
        
package flens.filter;

import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetFilter extends AbstractFilter {
    private Map<String, String> pairs = new HashMap<>();

    /**
     * @param name
     *            name under which this plugin is registered with the engine
     * @param plugin
     *            name of config that loaded this plugin (as registered in
     *            plugins.json)
     * @param tagger
     *            tagger used to mark output records
     * @param matcher
     *            matcher this filter should used to select recrods
     * @param prio
     *            plugin priority
     * @param fields
     *            fields to store results in
     * @param values
     *            values to set the fields to
     */
    public SetFilter(String name, String plugin, Tagger tagger, Matcher matcher, int prio, List<String> fields,
            List<String> values) {
        super(name, plugin, tagger, matcher, prio);
        for (int i = 0; i < fields.size(); i++) {
            this.pairs.put(fields.get(i), values.get(i));
        }
    }

    @Override
    public Collection<Record> process(Record in) {
        in.getValues().putAll(pairs);
        tag(in);
        return Collections.emptyList();
    }
}
