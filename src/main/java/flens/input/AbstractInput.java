package flens.input;

import java.util.concurrent.BlockingQueue;

import flens.core.Input;
import flens.core.Record;

public abstract class AbstractInput implements Input, Runnable {

	protected BlockingQueue<Record> in;
	protected Thread runner = new Thread(this);
	protected volatile boolean running;

	public void setInputQueue(BlockingQueue<Record> queue) {
		this.in = queue;
	}

	public void start() {
		running = true;
		runner.start();
	}

	public void stop() {
		running = false;
		runner.interrupt();
	}

	public void join() throws InterruptedException {
		runner.join();
	}

}
