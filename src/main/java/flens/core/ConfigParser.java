package flens.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.gson.Gson;

public class ConfigParser {

	private Flengine engine;
	// TODO use GSON to parse with costum objectbuilder
	protected Gson gson = new Gson();
	private PluginRepo pluginRepo;

	public ConfigParser() {
		this.engine = new Flengine();
		loadPlugins();
	}

	public Flengine getEngine() {
		return engine;
	}

	protected void load(Map map) {
		if (map == null)
			return;

		SortedMap<Integer, List<String>> keys = new TreeMap<>();

		for (Map.Entry entry : ((Map<String, Object>) map).entrySet()) {
			String key = (String) entry.getKey();

			Map<String, Object> value = (Map<String, Object>) entry.getValue();

			Integer prion = 5;

			if (value.containsKey("prio")) {
				Object prio = value.get("prio");
				if (prio instanceof String)
					prion = Integer.parseInt((String) prio);
				else
					prion = ((Number) prio).intValue();
			}
			if (!keys.containsKey(prion)) {
				keys.put(prion, new LinkedList<String>());
			}
			keys.get(prion).add(key);

		}

		for (List<String> xentry : keys.values()) {
			for (String entry : xentry) {
				Map<String, Object> child = (Map<String, Object>) map
						.get(entry);
				String plugin = (String) child.remove("plugin");
				if (plugin == null)
					plugin = (String) entry;

				Config c = pluginRepo.get(plugin);
				if (c == null) {
					throw new IllegalArgumentException("plugin not found: "
							+ entry);
				}

				c.readConfigPart((String) entry, child, engine);
			}
		}

	}

	protected void unload(Map map) {
		if (map == null)
			return;

		for (Object entry : map.keySet()) {

			String name = (String) entry;

			engine.remove(name);
		}

	}

	private void loadPlugins() {
		this.pluginRepo = new PluginRepo(getClass().getResourceAsStream(
				"/plugins.json"));
	}

	protected void construct(Map config) {
		load((Map) config.remove("input"));
		load((Map) config.remove("output"));
		load((Map) config.remove("filter"));
	}
}
