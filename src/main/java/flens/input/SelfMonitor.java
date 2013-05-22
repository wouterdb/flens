package flens.input;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import flens.core.Flengine;
import flens.core.Input;
import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractActiveInput;

//TODO make timer/executor based
public class SelfMonitor extends AbstractActiveInput implements Input {
	
	public static final String type = "flens";
	
	private Flengine engine;

	private long interval;

	public SelfMonitor(String name, Tagger tagger, Flengine e, int interval) {
		super(name,tagger);
		this.engine = e;
		this.interval = interval;
	}

	// TODO make configurable
	public void run() {
		try {
			while (running) {
				Record r = new Record();
				engine.report(r);
				dispatch(r);
				Thread.sleep(interval);

			}
		} catch (InterruptedException e) {
			// normal
		}

	}



}
