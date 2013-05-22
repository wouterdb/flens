package flens.config;

import java.util.LinkedList;
import java.util.List;

import flens.core.util.AbstractConfig;
import flens.input.OpenTsdbInput;

public class AMQPInput extends AbstractConfig {

	@Override
	protected void construct() {
		String host = get("host", "localhost");
		int port = getInt("port",5672);
		String vhost = get("vhost", null);
		String user = get("user","guest");
		String pass = get("pass","guest");
		String exchange = get("exchange",null);
		String queue = get("queue",null);
		String key = get("routingKey","*");
		
		engine.addInput(new flens.input.AMQPInput(name,tagger,host,port,vhost,user,pass,exchange,queue,key));
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
		out.add(new Option("host", "String", "localhost", "host to connect to"));
		out.add(new Option("port", "int", "4369", "port to connect to"));
		out.add(new Option("vhost", "String", null, "vhost"));
		out.add(new Option("user", "String", "guest", "username"));
		out.add(new Option("pass", "String", "guest", "password"));
		out.add(new Option("exchange", "String", null, "exchange to bind to"));
		out.add(new Option("queue", "String", null, "queue name"));
		out.add(new Option("routingKey", "String", "*", "routing key to bind queue to exchange"));
		return out;
	}


	@Override
	public String getDescription() {
		return "Listen on TCP socket for opentsdb messages";
	}

}