package flens.config;

import flens.core.util.AbstractConfig;

public class CharacterDecoder extends AbstractConfig{

	@Override
	protected boolean isIn() {
		return false;
	}

	@Override
	protected void construct() {
		engine.addFilter(new flens.filter.CharacterDecoder(name,tagger,matcher,prio));
	}

	@Override
	protected boolean isOut() {
		return false;
	}

	@Override
	public String getDescription() {
		return "decode binary messages";
	}

}
