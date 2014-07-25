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
package flens.core.util;

import java.util.List;
import java.util.Map;

import flens.core.Record;
import flens.core.Tagger;

public class StandardTagger implements Tagger {
	private List<String> tags;
	private List<?> rtags;
	private String type;

	private final String configprefix;

	public StandardTagger(String prefix, String type, List<String> tags,
			List<?> rtags) {
		this.tags = tags;
		this.rtags = rtags;
		this.type = type;

		this.configprefix = prefix;
	}

	@Override
	public void adapt(Record r) {
		r.getTags().addAll(tags);
		r.getTags().removeAll(rtags);
		if (type != null)
			r.setType(type);
	}

	@Override
	public void outputConfig(Map<String, Object> tree) {
		tree.put(configprefix + "add-tag", tags);
		tree.put(configprefix + "type", type);	
		tree.put(configprefix+"remove-tag",rtags);
	}

}
