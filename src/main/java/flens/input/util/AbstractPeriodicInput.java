package flens.input.util;

import java.util.Timer;
import java.util.TimerTask;

import flens.core.Tagger;

public abstract class AbstractPeriodicInput extends AbstractInput {

	protected Timer t;
	protected int interval;

	public AbstractPeriodicInput(String name, Tagger tagger,int interval) {
		super(name, tagger);
		this.interval = interval;
	}

	@Override
	public void start() {
		t = new Timer(true);
		t.scheduleAtFixedRate(getWorker(), 0, interval);
	}

	protected abstract TimerTask getWorker() ;

	@Override
	public void stop() {
		t.cancel();
	}

	@Override
	public void join() throws InterruptedException {
		
		
	}

}
