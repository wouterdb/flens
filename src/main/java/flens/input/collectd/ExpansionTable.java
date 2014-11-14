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
package flens.input.collectd;


import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;

import flens.core.Constants;
import flens.core.Record;

public class ExpansionTable {

	protected static class Mapping {
		public String plugin;
		public String type;
		public String[] names;

		public Mapping(String plugin, String type, String[] names) {
			super();
			this.plugin = plugin;
			this.type = type;
			this.names = names;
		}

		public String getPlugin() {
			return plugin;
		}

		public String getType() {
			return type;
		}

		public String[] getNames() {
			return names;
		}

		public boolean matches(String plugin, String type) {
			if (this.type != null)
				if (!this.type.equals(type))
					return false;
			if (this.plugin != null)
				if (!this.plugin.equals(plugin))
					return false;
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((plugin == null) ? 0 : plugin.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Mapping other = (Mapping) obj;
			if (plugin == null) {
				if (other.plugin != null)
					return false;
			} else if (!plugin.equals(other.plugin))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Mapping {plugin=");
			builder.append(plugin);
			builder.append(", type=");
			builder.append(type);
			builder.append(", names=");
			builder.append(Arrays.toString(names));
			builder.append("}");
			return builder.toString();
		}

	}

	private MultiMap<String,Mapping> mappings = new MultiValueMap<>();
	private Map<String,String[]> nullmap = new HashMap<String, String[]>();

	public ExpansionTable() {
		super();
	}

	protected void add(String plugin, String type, String[] names) {
		if (plugin == null)
			nullmap.put(type, names);
		else
			mappings.put(plugin, new Mapping(plugin, type, names));

	}

	public String[] resolve(String plugin, String type) {
		@SuppressWarnings("unchecked")
		Collection<Mapping> mapping = (Collection<Mapping>) mappings
				.get(plugin);
		if (mapping == null){
			String[] m = nullmap.get(type);
			return m;
		}
		for (Mapping m : mapping) {
			if (m.matches(plugin, type))
				return m.getNames();
		}
		return null;
	}

	public String[] resolve(Record rec) {
		return resolve((String) rec.getValues().get(CollectdConstants.PLUGIN), (String) rec
				.getValues().get(Constants.TYPE));
	}

	/*public void print() {
		for (Mapping m : (Collection<Mapping>) mappings.values()) {
			System.out.println(m.toString());
		}

	}*/

}