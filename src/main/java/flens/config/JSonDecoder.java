package flens.config;

import flens.core.util.AbstractConfig;

public class JSonDecoder extends AbstractConfig{

	@Override
	protected boolean isIn() {
		return false;
	}

	@Override
	protected void construct() {
		engine.addFilter(new flens.filter.JSonDecoder(name,tagger,matcher));
	}

	@Override
	protected boolean isOut() {
		return false;
	}

	@Override
	public String getDescription() {
		return "decode json messages";
	}

}
