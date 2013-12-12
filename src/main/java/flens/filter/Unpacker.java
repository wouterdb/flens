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

import flens.core.Filter;
import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

public class Unpacker extends AbstractFilter implements Filter{

	public Unpacker(String name, Tagger tagger, Matcher matcher,int prio) {
		super(name, tagger, matcher,prio);
	}

	@Override
	public Collection<Record> process(Record in) {
		List<Record> outs = new LinkedList<Record>();
		for(String name:in.getValues().keySet()){
			outs.add(makeRecord(in,name,in.getValues().get(name)));
		}
		in.setType(null);
		return outs;
	}

	private Record makeRecord(Record in, String name, Object value) {
		Record out = in.cloneNoValues();
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("metric", name);
		values.put("value", value);
		out.setValues(values);
		return tag(out);
	}

}
