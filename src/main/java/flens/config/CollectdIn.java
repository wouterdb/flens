package flens.config;

import java.util.LinkedList;
import java.util.List;

import flens.core.util.AbstractConfig;
import flens.input.OpenTsdbInput;
import flens.input.collectd.CollectdInput;

public class CollectdIn extends AbstractConfig {

	@Override
	protected void construct() {
		int port = getInt("port",25826);
		//todo more config and docs
		engine.addInput(new CollectdInput(name,tagger,port,null,null));
	}

	
	@Override
	protected boolean isIn() {
		return true;
	}

	@Override
	protected boolean isOut() {
		return false;
	}
	
	@Override
	public List<Option> getOptions() {
		List<Option>  out = new LinkedList(super.getOptions());
		out.add(new Option("port", "int", "25826", "port on which to listen"));
		return out;
	}


	@Override
	public String getDescription() {
		return "Listen on TCP socket for collectd messages";
	}

}
