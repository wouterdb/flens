package flens.config;

import flens.core.AbstractConfig;
import flens.input.OpenTsdbInput;

public class OpenTsdbIn extends AbstractConfig {

	@Override
	protected void construct() {
		int port = getInt("port",4242);
		engine.addInput(new OpenTsdbInput(name,tagger,port));
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
