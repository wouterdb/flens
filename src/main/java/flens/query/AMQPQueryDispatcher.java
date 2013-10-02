package flens.query;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import dnet.monitor.control.FactsHandler;
import dnet.monitor.control.PingHandler;
import dnet.monitor.control.amqp.CommandHandler;
import dnet.monitor.control.amqp.CommandServer;
import flens.core.ConfigHandler;
import flens.core.Flengine;
import flens.core.GenericQueryTerm;
import flens.core.Matcher;
import flens.core.Query;
import flens.core.QueryHandler;
import flens.core.Util;
import flens.core.util.AbstractPlugin;

/**
 * @author wouterdb
 * 
 * 
 *         misuse query handler interface for query server, ....
 */
public class AMQPQueryDispatcher extends AbstractPlugin implements QueryHandler {

	private Flengine engine;
	private ConnectionFactory factory;
	private CommandServer cs;
	private String name;
	private Connection conn;

	public AMQPQueryDispatcher(Flengine engine, String name, String host,
			int port, String vhost, String user, String pass) {
		this.engine = engine;
		this.name = name;
		factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setPort(port);
		factory.setUsername(user);
		factory.setPassword(pass);
		if (vhost != null)
			factory.setVirtualHost(vhost);

	}

	@Override
	public boolean canHandle(Query q) {
		return false;
	}

	@Override
	public void handle(Query q) {
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void start() {
		GenericQueryTerm qt = new GenericQueryTerm(engine);

		ConfigHandler ch = new ConfigHandler(engine);

		List<CommandHandler> chs = new LinkedList<>();
		chs.add(new PingHandler());
		chs.add(new FactsHandler());
		chs.add(ch);
		chs.add(qt);

		try {
			conn = factory.newConnection();
			cs = new CommandServer(Util.hostName(),conn ,
					engine.getTags(), chs);
			cs.enableHelp();
			cs.start();
		} catch (IOException e) {
			err("could not start command server ",e);
		}

	}

	@Override
	public void stop() {
		try {
			conn.close();
		} catch (IOException e) {
			err("could not stop command server ",e);
		}
	}

	@Override
	public void join() {
		
	}

}
