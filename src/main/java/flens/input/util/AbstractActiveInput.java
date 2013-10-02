package flens.input.util;

import java.util.concurrent.BlockingQueue;

import flens.core.Input;
import flens.core.Record;
import flens.core.Tagger;
import flens.core.util.AbstractPlugin;

public abstract class AbstractActiveInput extends AbstractInput implements Runnable {

	protected Thread runner = new Thread(this);
	protected volatile boolean running;

	public AbstractActiveInput(String name,Tagger tagger) {
		super(name, tagger);
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
