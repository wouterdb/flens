package flens.input.util;

import java.util.concurrent.BlockingQueue;

import flens.core.Record;
import flens.core.Tagger;

public class InputQueueExposer extends AbstractInput {

	private boolean stopped;

	public InputQueueExposer(String name,Tagger tagger) {
		super(name, tagger);
	}

	public void send(Record r) {
		dispatch(r);
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
		stopped = true;
	}

	@Override
	public void join() throws InterruptedException {
	}

	public boolean isStopped() {
		return stopped;
	}

}