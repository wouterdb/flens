package flens.config;

import java.util.List;

import flens.core.util.AbstractConfig;



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
		engine.addFilter(new flens.filter.Unpacker(name,tagger,matcher,prio));
	}

	

	@Override
	protected boolean isOut() {
		return false;
	}

	@Override
	public List<Option> getOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
