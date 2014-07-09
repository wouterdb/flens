package flens.config.util;

import java.util.Map;

/**
 * @author wouterdb
 *
 * marker for items capable of writing themselves out
 */
public interface ConfigWriteable {
	
	public void outputConfig(Map<String, Object> tree);
	

}
