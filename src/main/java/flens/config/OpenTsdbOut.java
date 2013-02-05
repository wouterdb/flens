package flens.config;

import flens.core.AbstractConfig;
import flens.output.OpenTsdbOutput;

public class OpenTsdbOut extends AbstractConfig{

	@Override
	protected boolean isIn() {
		
		return false;
	}
	
	@Override
	protected void construct() {
		String host = get("host", "localhost");
		int port = getInt("port", 4242);
		engine.addOutput(new OpenTsdbOutput(name,matcher,host,port));
	}

	@Override
	protected boolean isOut() {
		return true;
	}

}
