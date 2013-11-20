package flens.config;

import java.util.LinkedList;
import java.util.List;

import flens.core.Config.Option;
import flens.core.util.AbstractConfig;
import flens.input.OpenTsdbInput;

public class SocketOutput extends AbstractConfig {

	@Override
	protected void construct() {
		int port = getInt("port",19850);
		String host = get("host", "localhost");
		String field = get("field", "body");
		engine.addOutput(new flens.output.SocketOutput(name,matcher,host,port,field));
	}

	
	@Override
	protected boolean isIn() {
		return false;
	}

	@Override
	protected boolean isOut() {
		return true;
	}
	
	@Override
	public List<Option> getOptions() {
		List<Option>  out = new LinkedList(super.getOptions());
		out.add(new Option("port", "int", "19850", "port to connect to"));
		out.add(new Option("host", "String", "localhost", "host to connect to"));
		out.add(new Option("field", "String", "body", "field to get data from"));
		return out;
	}


	@Override
	public String getDescription() {
		return "Listen on TCP socket for opentsdb messages";
	}

}
