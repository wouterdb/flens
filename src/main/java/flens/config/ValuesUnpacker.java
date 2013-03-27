package flens.config;

import flens.core.util.AbstractConfig;

public class ValuesUnpacker extends AbstractConfig{
	
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

	@Override
	public String getDescription() {
		return "unpack all field into records of the form 'metric=$key, value=$value'";
	}

}
