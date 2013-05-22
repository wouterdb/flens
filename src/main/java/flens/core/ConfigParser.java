package flens.core;

import java.util.Map;

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

		for (Object entry : map.keySet()) {
			Map<String, Object> child = (Map<String, Object>) map.get(entry);
			String plugin = (String) child.remove("plugin");
			if (plugin == null)
				plugin = (String) entry;

			Config c = pluginRepo.get(plugin);
			if (c == null) {
				engine = null;
				throw new IllegalArgumentException("plugin not found: " + entry);
			}

			c.readConfigPart((String) entry, child, engine);
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
		this.pluginRepo = new PluginRepo(getClass().getResourceAsStream("/plugins.json"));
	}

	protected void construct(Map config) {
		load((Map) config.remove("input"));
		load((Map) config.remove("output"));
		load((Map) config.remove("filter"));
	}
}
