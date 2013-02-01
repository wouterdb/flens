package flens.config;

import flens.core.AbstractConfig;

public class SystemIn extends AbstractConfig{

	@Override
	protected boolean isIn() {
		return true;
	}

	@Override
	protected void construct() {
		engine.addInput(new flens.input.SystemIn(name,tagger));
	}

	@Override
	protected boolean isOut() {
		return false;
	}

}
