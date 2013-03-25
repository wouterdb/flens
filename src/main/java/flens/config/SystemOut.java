package flens.config;

import flens.core.util.AbstractConfig;

public class SystemOut extends AbstractConfig{

	@Override
	protected boolean isIn() {
		
		return false;
	}
	
	@Override
	protected void construct() {
		engine.addOutput(new flens.output.SystemOut(name,matcher));
	}

	@Override
	protected boolean isOut() {
		return true;
	}

	@Override
	public String getDescription() {
		return "send logs to system out" ;
	}

}
