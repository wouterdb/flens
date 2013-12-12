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
package flens;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import dnet.monitor.control.PingHandler;
import dnet.monitor.control.amqp.CommandHandler;
import dnet.monitor.control.amqp.CommandServer;

import flens.core.ConfigBuilder;
import flens.core.ConfigHandler;
import flens.core.Flengine;

public class Agent {

	public static void main(String[] args) throws IOException {

		ConfigHandler ch = new ConfigHandler();

		Gson g = new Gson();

		Map<String, Object> myconfig = g.fromJson(new FileReader(args[0]),
				HashMap.class);

		Map<String, String> tags = (Map<String, String>) myconfig.get("tags");
		
		Map<String, Object> initial = (Map<String, Object>) myconfig.get("init");

		String server = (String) myconfig.get("server");
		String name = (String) myconfig.get("name");

		System.out.println(String.format("connecting to %s as %s, with tags %s",server,name,tags));
		
		List<CommandHandler> chs = new LinkedList<>();
		chs.add(new PingHandler());
		chs.add(ch);

		ConnectionFactory c = new ConnectionFactory();
		c.setHost(server);
		c.setUsername("guest");
		c.setPassword("guest");
		c.setPort(5672);

		CommandServer cs = new CommandServer(name, c.newConnection(), tags, chs);
		cs.start();
		ch.load(initial);
		ch.getEngine().addTags(tags);
		ch.getEngine().start();
	}

}
