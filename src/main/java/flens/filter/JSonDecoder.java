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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JSonDecoder extends AbstractFilter {

    private Gson decoder;
    private boolean inlist;

    public JSonDecoder(String name, String plugin, Tagger tagger, Matcher matcher, int prio, boolean inlist) {
        super(name, plugin, tagger, matcher, prio);
        decoder = (new GsonBuilder()).serializeSpecialFloatingPointValues().create();
        this.inlist = inlist;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Record> process(Record in) {
        // TODO config
        try {
            if (inlist) {
                List<Map<String, Object>> values = decoder.fromJson((String) in.get("message"), List.class);
                if (values.size() > 1) {
                    warn("multiple records in single json message, may produce unexpected behavior");
                    in.getValues().putAll(values.get(0));
                    tag(in);

                    List<Record> out = new LinkedList<Record>();
                    for (int i = 1; i < values.size(); i++) {

                        out.add(tag(Record.createWithValues(values.get(i))));
                    }

                    return out;
                } else {
                    in.getValues().putAll(values.get(0));
                    tag(in);
                    return Collections.emptyList();
                }

            } else {
                Map<String, Object> values = decoder.fromJson((String) in.get("message"), HashMap.class);
                if (values != null) {
                    in.getValues().putAll(values);
                }
                tag(in);
                return Collections.emptyList();
            }

        } catch (Exception e) {
            warn("could not parse json {0}", ((String) in.get("message")).replace("\n", " "));
            return Collections.emptyList();
        }
    }

    
    
}
