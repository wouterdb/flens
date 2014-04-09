package flens.config.util;

import flens.core.Flengine;

/**
 * @author wouterdb
 *
 * marker interface for things that can be built through the config reflector
 */
public interface Reflectable{
	
	public void init(Flengine engine, String name);

}
