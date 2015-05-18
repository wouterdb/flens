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

import flens.core.Constants;
import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RenameFilter extends AbstractFilter {
    private List<Pair<String, String>> names = new LinkedList<>();

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
     * @param from
     *            fields to rename
     * @param to
     *            new names for fields
     */
    public RenameFilter(String name, String plugin, Tagger tagger, Matcher matcher, int prio, List<String> from,
            List<String> to) {
        super(name, plugin, tagger, matcher, prio);
        for (int i = 0; i < from.size(); i++) {
            this.names.add(Pair.of(from.get(i), to.get(i)));
        }
    }

    @Override
    public Collection<Record> process(Record in) {
        Map<String, Object> vals = in.getValues();
        for (Pair<String, String> ren : this.names) {
            Object val = vals.remove(ren.getKey());
            if (val != null) {
                if (!(ren.getRight()).isEmpty()) {
                    vals.put(ren.getRight(), val);
                }
                if (ren.getRight().equals(Constants.TIME)) {

                    vals.put(Constants.TIME, in.getTimestamp());
                }
            }
        }

        tag(in);
        return Collections.emptyList();
    }
}
