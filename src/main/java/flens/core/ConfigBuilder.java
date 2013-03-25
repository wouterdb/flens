package flens.core;

import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class ConfigBuilder {
	
	//TODO use GSON to parse with costum objectbuilder
	
	private Flengine engine;
	private Gson gson = new Gson();
	private PluginRepo pluginRepo;
	private Map config;
	
	public ConfigBuilder(Reader is) {
		config = gson.fromJson(is, HashMap.class);
	}

	public void run() {
		this.engine = new Flengine();
		loadPlugins();
		construct();
	}

	public Flengine getEngine(){
		return engine;
	}
	
	private void construct() {
		load((Map)config.remove("input"));
		load((Map)config.remove("output"));
		load((Map)config.remove("filter"));
	}

	private void load(Map map) {
		if(map == null)
			return;
		
		for (Object entry : map.keySet()) {
			Map<String, Object> child = (Map<String, Object>) map.get(entry);
			String plugin = (String) child.remove("plugin");
			if(plugin == null)
				plugin = (String) entry;
			
			Config c = pluginRepo.get(plugin);
			if(c==null){
				engine = null;
				throw new IllegalArgumentException("plugin not found: " + entry);
			}
				
			c.readConfigPart((String)entry,child, engine);
		}
		
	}

	private void loadPlugins() {
		this.pluginRepo = new PluginRepo(getClass().getResourceAsStream("/plugins.json"));
	}

}
