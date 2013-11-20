package flens.config;

import java.util.LinkedList;
import java.util.List;

import flens.core.util.AbstractConfig;
import flens.input.OpenTsdbInput;

public class SocketInput extends AbstractConfig {

	@Override
	protected void construct() {
		int port = getInt("port",19850);
		engine.addInput(new flens.input.SocketInput(name,tagger,port));
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
		out.add(new Option("port", "int", "19850", "port to connect to"));
		return out;
	}


	@Override
	public String getDescription() {
		return "Listen on TCP socket for opentsdb messages";
	}

}
