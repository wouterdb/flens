/**
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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import flens.core.Filter;
import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

public class Splitter extends AbstractFilter implements Filter {

	List<String> keys = new LinkedList<>();
	private boolean keeprest;

	public Splitter(String name, Tagger tagger, Matcher matcher, int prio,
			List<String> keys, boolean keeprest) {
		super(name, tagger, matcher, prio);
		this.keys = keys;
		this.keeprest = keeprest;
	}

	@Override
	public Collection<Record> process(Record in) {
		List<Record> outs = new LinkedList<Record>();

		Map<String, Object> pairs = new HashMap<>();

		for (String name : keys) {
			Object x = in.getValues().remove(name);
			if(x instanceof Map){
				Map<String,Object> m = (Map<String,Object>)x;
				for(Map.Entry<String,Object> entr:m.entrySet()){
					pairs.put(entr.getKey(),entr.getValue());
				}
			}else
				pairs.put(name, x);
		}

		for (Map.Entry<String, Object> entries : pairs.entrySet()) {
			outs.add(makeRecord(in, entries.getKey(), entries.getValue()));
		}
		if (!keeprest)
			// kill record
			in.setType(null);
		return outs;
	}

	private Record makeRecord(Record in, String name, Object value) {
		Record out = in.doClone();
		Map<String, Object> values = out.getValues();
		values.put("metric", name);
		values.put("value", value);
		return tag(out);
	}

}
