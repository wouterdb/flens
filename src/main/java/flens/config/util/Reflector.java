package flens.config.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
			for (Field f : getFields(tostore.getClass())) {
				f.setAccessible(true);
				Object o = f.get(tostore);
				
				if (o instanceof ConfigWriteable) {
					((ConfigWriteable) o).outputConfig(out);
				} else {
					
					Option opt = optmap.get(f.getName());
					if (opt != null) {
						assertPremissable(o);
						out.put(f.getName(), o);
					}
				}

			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException("bad config description", e);
		}
		return out;
	}

	private static void assertPremissable(Object o) {
		if(o == null)
			return;
		if(o instanceof Collection){
			for(Object c:(Collection)o){
				assertPremissable(c);
			}
		}else if(o instanceof Map){
			for(Map.Entry c:((Map<Object,Object>)o).entrySet()){
				if(!(c.getKey() instanceof String))
					throw new IllegalArgumentException("key of map is not string" + c.getKey());
				assertPremissable(c.getValue());
			}
		}else{
			if(!(o instanceof Number || o instanceof String || o instanceof Boolean))
				throw new IllegalArgumentException("type not permissible" + o);
			
		}
		
	}

	private static List<Field> getFields(Class<? extends Object> class1) {
			List<Field> acc = new LinkedList<>();
			getFields(acc,class1);
			return acc;
	}

	private static void getFields(List<Field> acc, Class<? extends Object> class1) {
		if(class1==null)
			return;
		getFields(acc,class1.getSuperclass());
		
		acc.addAll(Arrays.asList(class1.getDeclaredFields()));
		
	}

}
