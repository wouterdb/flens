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
