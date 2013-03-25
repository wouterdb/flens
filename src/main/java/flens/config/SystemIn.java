package flens.config;

import flens.core.util.AbstractConfig;

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

	@Override
	public String getDescription() {
		return "Add lines from system in as records";
	}

}
