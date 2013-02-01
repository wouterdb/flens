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
		load((Map)config.get("inputs"));
		load((Map)config.get("outputs"));
		if(config.containsKey("filter")){
			load((Map)config.get("filter"));
		}
	}

	private void load(Map map) {
		for (Object entry : map.keySet()) {
			Config c = pluginRepo.get((String) entry);
			c.readConfigPart(map.get(entry), engine);
		}
		
	}

	private void loadPlugins() {
		this.pluginRepo = new PluginRepo(getClass().getResourceAsStream("plugins.json"));
	}

}
