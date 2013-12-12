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

import flens.core.Constants;
import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.tuple.Pair;

public class RenameFilter extends AbstractFilter {
	private List<Pair<String, String>> names = new LinkedList<>();

	public RenameFilter(String name, Tagger tagger, Matcher matcher,int prio,
			List<String> f, List<String> t) {
		super(name, tagger, matcher,prio);
		for (int i = 0; i < f.size(); i++)
			this.names.add(Pair.of(f.get(i), t.get(i)));
	}

	public Collection<Record> process(Record in) {
		Map vals = in.getValues();
		for (Pair ren : this.names) {
			Object val = vals.remove(ren.getKey());
			if (val != null) {
				if(!((String)ren.getRight()).isEmpty())
					vals.put(ren.getRight(), val);
				if(ren.getRight().equals(Constants.TIME)){
					
					vals.put(Constants.TIME, in.getTimestamp());
				}
			}
		}
		
		tag(in);
		return Collections.EMPTY_LIST;
	}
}
