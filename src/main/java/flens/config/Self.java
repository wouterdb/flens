package flens.config;

import flens.core.AbstractConfig;
import flens.input.SelfMonitor;

public class Self extends AbstractConfig{

	@Override
	protected void construct() {
		int interval = getInt("interval", 60000);
		engine.addInput(new SelfMonitor(name,tagger,engine,interval));
		
	}

	@Override
	protected boolean isIn() {
		return true;
	}

	@Override
	protected boolean isOut() {
		return false;
	}

}
