package flens.core;

import java.util.Map;

public interface Config {
	
	public void readConfigPart(Map<String, Object> tree, Flengine engine);

	public void readConfigPart(Object value, Flengine engine);
	
	

}
