package flens.core;

import java.util.Set;

public interface Plugin {
	
	/**
	 * idempotent, fast
	 */
	public Matcher getMatcher();
	public String getName();
	

}
