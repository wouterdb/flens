package flens.config;

import java.util.LinkedList;
import java.util.List;

import flens.core.util.AbstractConfig;
import flens.input.GraphiteInput;
import flens.input.OpenTsdbInput;

public class GraphiteIn extends AbstractConfig {

	@Override
	protected void construct() {
		int port = getInt("port",2003);
		engine.addInput(new GraphiteInput(name,tagger,port));
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
		out.add(new Option("port", "int", "2003", "port on which to listen"));
		return out;
	}


	@Override
	public String getDescription() {
		return "Listen on TCP socket for graphite messages";
	}

}
