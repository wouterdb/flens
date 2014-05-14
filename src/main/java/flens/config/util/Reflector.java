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

	public static Reflectable contruct(Flengine engine, Reflectable r, Config conf, String name,
			Map<String, Object> tree) {
		for (Option opt : conf.getOptions()) {
			loadIfPresent(r, tree, opt);
		}
		r.init(engine, name);
		return r;
	}

	private static void loadIfPresent(Reflectable r, Map<String, Object> tree, Option opt) {
		String name = opt.getName();

		Object x = tree.get(name);

		if (x == null) {
			x = opt.getDefaultv();
			if (x == null)
				throw new IllegalArgumentException("obligatory option not present" + name);
		}

		Map<String, Field> fields = collectFields(r.getClass());

		Field f = fields.get(name);
		if (f == null)
			throw new IllegalStateException("bad config description, field not found: " + name);
		f.setAccessible(true);
		try {
			f.set(r, x);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static LRUMap fieldcache = new LRUMap(64);
	
	private static Map<String, Field> getFields(Class clazz) {
		Map<String, Field> out = (Map<String, Field>) fieldcache.get(clazz);
		if(out!=null)
			return out;
		out = collectFields(clazz);
		fieldcache.put(clazz, out);
		return out;
	}
	
	private static Map<String, Field> collectFields(Class clazz) {
		if (clazz == null)
			return new HashMap<>();
		Map<String, Field> out = collectFields(clazz.getSuperclass());

		for (Field f : clazz.getDeclaredFields()) {
			if((f.getModifiers() & Modifier.STATIC) == 0)
				out.put(f.getName(), f);
		}

		return out;
	}

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
