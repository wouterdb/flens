package flens.core;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public interface Input {

	public void setInputQueue(BlockingQueue<Record> queue);
	public void start();
	public void stop();
	/**
	 * await stop
	 * @throws InterruptedException 
	 */
	public void join() throws InterruptedException;
	
}
