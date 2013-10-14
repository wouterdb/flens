package flens.query;

import flens.core.Query;
import flens.core.QueryHandler;
import flens.core.util.AbstractPlugin;

public class PingQuery extends AbstractPlugin implements QueryHandler {

	private String name;
	
	public PingQuery(String name) {
		this.name = name;
	}

	@Override
	public boolean canHandle(Query q) {
		return q.getQuery().startsWith("ping://") ;
	}

	@Override
	public void handle(Query q) {
		q.respond("pong " + System.currentTimeMillis());
		
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void join() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return name;
	}

	

}
