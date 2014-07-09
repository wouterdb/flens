package flens.input.util;

import java.util.concurrent.BlockingQueue;

import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractInput;

public class InputQueueExposer extends AbstractInput {

	private boolean stopped;

	public InputQueueExposer(String name,String plugin, Tagger tagger) {
		super(name,plugin, tagger);
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
