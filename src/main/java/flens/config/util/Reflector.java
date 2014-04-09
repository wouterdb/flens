package flens.config.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import flens.core.Config;
import flens.core.Flengine;
import flens.core.Config.Option;

public class Reflector {

	public static Reflectable contruct(Flengine engine, Reflectable r,
			Config conf, String name, Map<String, Object> tree) {
		for (Option opt : conf.getOptions()) {
			loadIfPresent(r, tree, opt);
		}
		r.init(engine, name);
		return r;
	}

	private static void loadIfPresent(Reflectable r, Map<String, Object> tree,
			Option opt) {
		String name = opt.getName();

		Object x = tree.get(name);

		if (x == null) {
			x = opt.getDefaultv();
			if (x == null)
				throw new IllegalArgumentException(
						"obligatory option not present" + name);
		}

		Field f = null;
		try {
			f = r.getClass().getField(name);
			f.setAccessible(true);
			f.set(r, x);
		} catch (Exception e) {
			throw new IllegalStateException("bad config description " + name, e);
		}

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
				if(o instanceof ConfigWriteable){
					((ConfigWriteable)o).outputConfig(out);
				}else{
					Option opt = optmap.get(f.getName());
					if(opt!=null && !o.toString().equals(opt.getDefaultv())){
						out.put(f.getName(), o);
					}
				}

			}
		} catch (IllegalArgumentException|IllegalAccessException e) {
			throw new IllegalStateException("bad config description", e);
		} 
		return out;
	}

}
