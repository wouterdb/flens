/**
 *
 *     Copyright 2013 KU Leuven Research and Development - iMinds - Distrinet
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     Administrative Contact: dnet-project-office@cs.kuleuven.be
 *     Technical Contact: wouter.deborger@cs.kuleuven.be
 */
package flens.config;

import java.util.LinkedList;
import java.util.List;

import flens.config.util.AbstractConfig;
import flens.input.OpenTsdbInput;

public class AMQPOutput extends AbstractConfig {

	@Override
	protected void construct() {
		String field = get("field","amqp"); 
		String host = get("host", "localhost");
		int port = getInt("port",5672);
		String vhost = get("vhost", null);
		String user = get("user","guest");
		String pass = get("pass","guest");
		String exchange = get("exchange","metrics");
		String key = get("routingKey","fles");
		engine.addOutput(new flens.output.AMQPOut(name,plugin,matcher,field,host,port,vhost,user,pass,exchange,key));
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
		out.add(new Option("field", "String", "amqp", "field from which to get body"));
		out.add(new Option("host", "String", "localhost", "host to connect to"));
		out.add(new Option("port", "int", "4369", "port to connect to"));
		out.add(new Option("vhost", "String", null, "vhost"));
		out.add(new Option("user", "String", "guest", "username"));
		out.add(new Option("pass", "String", "guest", "password"));
		out.add(new Option("exchange", "String", null, "exchange to bind to"));
		out.add(new Option("routingKey", "String", "*", "routing key to bind queue to exchange"));
		return out;
	}


	@Override
	public String getDescription() {
		return "Listen on TCP socket for opentsdb messages";
	}

}
