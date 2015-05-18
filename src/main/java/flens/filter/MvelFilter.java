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
import flens.util.MvelUtil;

import org.mvel2.MVEL;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

public class MvelFilter extends AbstractFilter {

    private String script;
    private Serializable compiled;

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
     * @param script
     *            mvel script to apply, see Config documentation for more
     *            details
     */
    public MvelFilter(String name, String plugin, Tagger tagger, Matcher matcher, int prio, String script) {
        super(name, plugin, tagger, matcher, prio);
        this.script = script;
        start();
    }

    private void start() {
        // Compile the expression.
        compiled = MVEL.compileExpression(script, MvelUtil.getTooledContext());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Record> process(Record in) {
        in.getValues().put("DISCARD", false);
        Object records = MVEL.executeExpression(compiled, in.getValues());

        tag(in);

        Object discard = in.getValues().remove("DISCARD");
        if (discard instanceof Boolean && ((Boolean) discard)) {
            in.setType(null);
        }

        if (records == null) {
            return Collections.emptyList();
        }
        if (records instanceof Record) {
            return Collections.singletonList((Record) records);
        }
        if (records instanceof Collection) {
            Collection<Record> collection = (Collection<Record>) records;
            if (collection.isEmpty()) {
                return Collections.emptyList();
            }
            Object record = collection.iterator().next();
            if (!(record instanceof Record)) {
                warn("mvel returned wrong type in list " + record.getClass().getName()
                        + " should be list of records or record");
                return Collections.emptyList();
            }
            return collection;
        }

        warn("mvel returned wrong type " + records.getClass().getName() + " should be list of records or record");
        return Collections.emptyList();
    }

}
