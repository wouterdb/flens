package flens.config.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;

import flens.core.Config;
import flens.core.Flengine;
import flens.core.Config.Option;

public class Reflector {

	public static Map<String, Object> store(Object tostore, Config conf) {
		Map<String, Object> out = new HashMap<>();
		Map<String, Option> optmap = new HashMap<>();
		for (Option opt : conf.getOptions()) {
			optmap.put(opt.getName(), opt);
		}
		try {
			for (Field f : tostore.getClass().getFields()) {

				Object o = f.get(tostore);
				if (o instanceof ConfigWriteable) {
					((ConfigWriteable) o).outputConfig(out);
				} else {
					Option opt = optmap.get(f.getName());
					if (opt != null && !o.toString().equals(opt.getDefaultv())) {
						out.put(f.getName(), o);
					}
				}

			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException("bad config description", e);
		}
		return out;
	}

}
