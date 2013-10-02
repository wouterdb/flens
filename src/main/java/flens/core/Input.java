package flens.core;

import java.util.concurrent.BlockingQueue;

public interface Input {

	public String getName();
	
	public void setInputQueue(BlockingQueue<Record> queue);
	public void start();
	public void stop();
	/**
	 * await stop
	 * @throws InterruptedException 
	 */
	public void join() throws InterruptedException;
	
	
	public int getRecordsSent();
}
