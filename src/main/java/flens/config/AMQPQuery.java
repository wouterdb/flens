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
