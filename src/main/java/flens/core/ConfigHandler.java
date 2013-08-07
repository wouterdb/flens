package flens.core;

import java.util.Map;
import java.util.UUID;

import dnet.monitor.control.Command;
import dnet.monitor.control.config.ConfigClient;

public class ConfigHandler extends ConfigClient {
	
	public ConfigParser parser = new ConfigParser(); 

	@Override
	public void configRemoved(UUID id, Map<String, Object> cfg) {
		System.out.println("removing config: " + id);
		parser.unload(cfg);
		
	}

	@Override
	public void configUpdated(Command command) {
		parser.unload(command.getConfig());
		parser.load(command.getConfig());
	}

	@Override
	public void configAdded(Command command) {
		System.out.println("adding config: " + command);
		parser.load(command.getConfig());
	}

	public Flengine getEngine(){
		return parser.getEngine();
	}

	public void load(Map<String, Object> initial) {
		parser.construct(initial);
		configs.put(new UUID(0,0),initial);
	}

}
