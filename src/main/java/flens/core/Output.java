package flens.core;

import java.util.Queue;

public interface Output extends Plugin {

	/**
	 * @return the output queue
	 * 
	 * method is idempotent
	 */
	public Queue<Record> getOutputQueue();
	public void start();
	public void stop();
	
	
	//stats, may be an underestimate, due to threading
	//prefer loss to lock
	public int getRecordsSent();
	public int getRecordsLost();
	
}
