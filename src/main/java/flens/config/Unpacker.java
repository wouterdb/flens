package flens.config;

import flens.core.AbstractConfig;

public class Unpacker extends AbstractConfig{
	
	enum Modes{
		
	}

	@Override
	protected boolean isIn() {
		
		return false;
	}
	
	@Override
	protected void construct() {
		requiresLoopFree();
		engine.addFilter(new flens.filter.Unpacker(name,tagger,matcher));
	}

	

	@Override
	protected boolean isOut() {
		return false;
	}

}
