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
package flens.core;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import dnet.help.HelpBuilder;
import dnet.monitor.control.Command;
import dnet.monitor.control.amqp.CommandHandler;
import dnet.monitor.control.amqp.CommandServer;
import dnet.monitor.control.query.QueryTerminator;

public class GenericQueryTerm implements CommandHandler {
	
	private Flengine engine;

	public GenericQueryTerm(Flengine engine) {
		this.engine = engine;
	}

	public class GenericQuery extends Query {

		private Command command;
		private CommandServer server;

		public GenericQuery(Command command, CommandServer server) {
			super((String) command.getConfig().get("url"), command.getConfig());
			this.command = command;
			this.server = server;
		}

		@Override
		public void respond(byte[] payload) {
			try {
				server.reply(command, payload, new HashMap<String, Object>());
			} catch (IOException e) {
				Logger.getLogger("flens.core.query").log(Level.SEVERE,
						"failed to send reply", e);
			}

		}

		@Override
		public void respond(String payload) {
			try {
				server.reply(command, payload, new HashMap<String, Object>());
			} catch (IOException e) {
				Logger.getLogger("flens.core.query").log(Level.SEVERE,
						"failed to send reply", e);
			}

		}

		@Override
		public void fail(int code, String msg) {
			server.err(command, code, msg);

		}

	}

	@Override
	public String prefix() {
		return "query";
	}

	@Override
	public void handle(Command command, CommandServer server) {
		if (command.getCommand().equals("query.do")) {
			doQ(command, server);
		}
	}

	private void doQ(Command command, CommandServer server) {

		Query q = new GenericQuery(command, server);
		List<QueryHandler> qh = engine.getHandler(q);
		
		if(qh.isEmpty()){
			q.fail(501, "query type unknown");
		}else{
			for (QueryHandler queryHandler : qh) {
				queryHandler.handle(q);
			}
		}
			
		
	}

	@Override
	public String version() {
		return "1.0";
	}

	@Override
	public String help() {
		return HelpBuilder.forHandler(this).add("do", "address query plugin").add("help", "get query help").build();
	}

	
}
