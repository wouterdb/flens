package flens.config;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import flens.core.Config.Option;
import flens.core.util.AbstractConfig;
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
		List<String> stags = getArray("send-tags",new LinkedList<String>());
		engine.addOutput(new OpenTsdbOutput(name,matcher,host,port,(stags)));
	}

	@Override
	protected boolean isOut() {
		return true;
	}
	
	@Override
	public List<Option> getOptions() {
		List<Option>  out = new LinkedList(super.getOptions());
		out.add(new Option("port", "int", "4242", "port to which to connect"));
		out.add(new Option("host", "String", "localhost", "host to which to connect"));
		out.add(new Option("send-tags", "[String]", "[]", "field to append as tags, use [fliedname:tag-name] to rename"));
		return out;
	}
	
	@Override
	public String getDescription() {
		return "send out records to an opentsdb server \n send out messages of the form \n put ${metric} ${timestamp/1000} ${value} host=${source}";
	}

}
