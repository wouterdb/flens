package flens.config;

import java.util.LinkedList;
import java.util.List;

import flens.core.util.AbstractConfig;
import flens.input.OpenTsdbInput;
import flens.query.AMQPQueryDispatcher;

public class AMQPQuery extends AbstractConfig {

	@Override
	protected void construct() {
		String host = get("host", "localhost");
		int port = getInt("port",5672);
		String vhost = get("vhost", null);
		String user = get("user","guest");
		String pass = get("pass","guest");
		
		engine.addHandler(new AMQPQueryDispatcher(engine,name,host,port,vhost,user,pass));
	}

	
	@Override
	protected boolean isIn() {
		return false;
	}

	@Override
	protected boolean isOut() {
		return false;
	}
	
	protected boolean isQuery(){
		return true;
	}
	
	protected boolean isFilter() {
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
		
		return out;
	}


	@Override
	public String getDescription() {
		return "Listen on AMQP for commands and queries";
	}

}
