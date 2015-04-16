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

package flens.filter;

import flens.core.Constants;
import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;
import flens.input.collectd.CollectdConstants;
import flens.input.collectd.CollectdTypeingTable;
import flens.input.collectd.TypeingTable.Mapping;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CollectdDecoder extends AbstractFilter {

    public CollectdDecoder(String name, String plugin, Tagger tagger, Matcher matcher, int prio) {
        super(name, plugin, tagger, matcher, prio);
    }

    @Override
    public Collection<Record> process(Record in) {
        List<Record> out = expandAndDispatch(in);

        in.setType(null);

        for (Record r : out) {
            tag(r);
        }

        return out;
    }

    private final CollectdTypeingTable cdet = new CollectdTypeingTable();

    /**
     * collectd has multi-valued metrics. The values are not self-descriptive
     */
    @SuppressWarnings("unchecked")
    private List<Record> expandAndDispatch(Record rec) {
        rec.setSource((String) rec.getValues().remove("host"));
        rec.setTimestamp((long)((Double) rec.getValues().remove("time") * 1000));
        List<Number> values = (List<Number>) rec.getValues().get(CollectdConstants.VALUES);
        int size = values.size();

        Mapping typeing = cdet.resolve(rec);
        if (typeing == null) {
            return failAndDispatch(rec, "multi valued record not exanded");

        }

        if (size != typeing.getNames().length) {
            return failAndDispatch(rec, "multi valued record has unexpected number of values");

        }

        return normalizeAndDispatch(rec, values, typeing);

    }

    private List<Record> normalizeAndDispatch(Record rec, List<Number> values, Mapping typeing) {
        rec.getValues().remove(CollectdConstants.VALUES);
        rec = rec.doClone();

        // cut out collectd specific parts
        rec.getValues().remove(CollectdConstants.PLUGIN);
        Object instance = rec.getValues().remove(CollectdConstants.PLUGIN_INSTANCE);
        rec.getValues().remove(CollectdConstants.TYPE_INSTANCE);
        if (instance != null) {
            rec.getValues().put(Constants.INSTANCE, instance);
            rec.getValues().put(Constants.TYPE, typeing.plugin);
        } else {
            rec.getValues().remove(Constants.TYPE);
        }

        List<Record> outc = new LinkedList<Record>();

        for (int i = 0; i < typeing.names.length; i++) {
            Number value = values.get(i);
            Record out = rec.doClone();
            out.getValues().put(Constants.VALUE, value);
            out.getValues().put(Constants.METRIC, typeing.names[i]);

            out.addMeta(typeing.otype[i]);
            outc.add(out);
        }

        return outc;

    }

    private List<Record> failAndDispatch(Record rec, String msg) {
        err(msg + ":" + rec.toString());
        return Collections.singletonList(rec.doClone());
    }

}
