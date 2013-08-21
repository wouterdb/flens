package flens.config;

import java.util.LinkedList;
import java.util.List;

import flens.core.Config.Option;
import flens.core.util.AbstractConfig;
import flens.input.SelfMonitor;

public class PingQuery extends AbstractConfig{

	@Override
	protected void construct() {
		engine.addHandler(new flens.query.PingQuery(name));	
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
		return "ping query";
	}

}
