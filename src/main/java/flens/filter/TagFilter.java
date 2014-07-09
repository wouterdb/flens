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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import flens.core.Constants;
import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

public class TagFilter extends AbstractFilter {

	private Map<String, String> mytags;


	public TagFilter(String name,String plugin, Tagger tagger, Matcher matcher, int prio,
			Map<String, String> tags) {
		super(name, plugin,tagger, matcher, prio);

		this.mytags = tags;
	}

	@Override
	public Collection<Record> process(Record in) {
		if (in.getValues().containsKey(Constants.TAGS)) {
			//nothing to do
			//todo make configurable
		} else {
			in.setValue(Constants.TAGS, mytags);
		}
		return Collections.EMPTY_LIST;
	}

}
