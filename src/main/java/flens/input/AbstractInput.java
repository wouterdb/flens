package flens.input;

import java.util.concurrent.BlockingQueue;

import flens.core.Input;
import flens.core.Record;
import flens.core.Tagger;

public abstract class AbstractInput implements Input, Runnable {

	protected BlockingQueue<Record> in;
	protected Thread runner = new Thread(this);
	protected volatile boolean running;
	private Tagger tagger;
	private String name;

	public AbstractInput(String name,Tagger tagger) {
		this.name = name;
		this.tagger = tagger;
	}

	
	@Override
	public String getName() {
		return name;
	}
	
	protected void dispatch(Record r){
		tagger.adapt(r);
		in.add(r);
	}
	
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
