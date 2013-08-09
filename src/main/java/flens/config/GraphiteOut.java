package flens.config;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import flens.core.Config.Option;
import flens.core.util.AbstractConfig;
import flens.output.GraphiteOutput;
import flens.output.OpenTsdbOutput;

public class GraphiteOut extends AbstractConfig{
	
	

	private static final String DEFAULT_METRIC =  "@{metric}@{(isdef type)?'.'+type:''}.@{reverseHostname(source)}";

	@Override
	protected boolean isIn() {
		
		return false;
	}
	
	@Override
	protected void construct() {
		String host = get("host", "localhost");
		int port = getInt("port", 2003);
		String template =  get("metric",DEFAULT_METRIC);
		engine.addOutput(new GraphiteOutput(name,matcher,host,port,template));
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
		out.add(new Option("metric", "String", DEFAULT_METRIC, "mvel template to use as metric name for graphite"));
		return out;
	}
	
	@Override
	public String getDescription() {
		return "send out records to an graphite server \n send out messages of the form \n ${metric} ${value} ${timestamp/1000}";
	}

}
