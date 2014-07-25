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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import flens.core.Matcher;
import flens.core.Record;

public class StandardMatcher implements Matcher {

	private Set<String> tags;
	private String type;

	public StandardMatcher(String type, List<String> tags) {
		this.type = type;
		this.tags = new HashSet<String>(tags);
	}

	@Override
	public boolean matches(Record r) {
		if(type != null && !type.equals(r.getType()))
			return false;
		return r.getTags().containsAll(tags);
			
	}

	@Override
	public void outputConfig(Map<String, Object> tree) {
		tree.put("type", type);
		tree.put("tags", tags);
	}

}
