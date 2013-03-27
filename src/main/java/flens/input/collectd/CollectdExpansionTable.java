package flens.input.collectd;

import java.util.Collection;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

import flens.core.Record;
import static flens.core.Constants.*;

public class CollectdExpansionTable {

	public CollectdExpansionTable() {
		init();
	}
	
	private static class Mapping {
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

		public boolean matches(Record r){
			if(type!=null)
				if(!type.equals(r.getValues().get(TYPE)))
					return false;
			if(plugin!=null)
				if(!plugin.equals(r.getValues().get(PLUGIN)))
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

	}

	private MultiMap mappings = new  MultiValueMap();
	
	
	protected void init() {
		add("interface", null, new String[] { "in", "out" });
		add("load", null, new String[] { "1m", "5m","15m" });

	}

	private void add(String plugin, String type, String[] names) {
		mappings.put(plugin, new Mapping(plugin, type, names));
	}

	
	public String[] resolve(Record r){
		Collection<Mapping> mapping = (Collection<Mapping>)mappings.get(r.getValues().get(PLUGIN));
		if(mapping == null)
			return null;
		for(Mapping m:mapping){
			if(m.matches(r))
				return m.getNames();
		}
		return null;
	}
}
