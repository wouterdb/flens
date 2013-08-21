package flens.config;

import java.util.LinkedList;
import java.util.List;

import flens.core.Config.Option;
import flens.core.util.AbstractConfig;
import flens.input.SelfMonitor;

public class JMXQuery extends AbstractConfig{

	@Override
	protected void construct() {
		String host = get("host", "localhost");
		int port = getInt("port",9999);
		engine.addHandler(new flens.query.JMXQuery(name,host,port));	
	}

	@Override
	protected boolean isIn() {
		return false;
	}

	@Override
	protected boolean isOut() {
		return false;
	}
	
	@Override
	protected boolean isQuery() {
		return true;
	}

	@Override
	public String getDescription() {
		return "JMX query";
	}

}
