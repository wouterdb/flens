package flens.core;

public interface QueryHandler {
	
	public boolean canHandle(Query q);
	public void handle(Query q);
	
	public String getName();
	
	public void start();
	public void stop();
	public void join();

}
