package flens.core;

import java.util.Map;

public interface Config {
	
	public void readConfigPart(String name, Map<String, Object> tree, Flengine engine);

	
	
	

}
