package flens.input;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import flens.core.Flengine;
import flens.core.Input;
import flens.core.Record;

public class SelfMonitor extends AbstractInput implements Input {
	
	public static final String type = "flens";
	
	
	
	private Flengine engine;

	public SelfMonitor(Flengine e) {
		this.engine = e;
	}

	// TODO make configurable
	public void run() {
		try {
			while (running) {
				Record r = new Record(type);
				engine.report(r);
				in.add(r);
				Thread.sleep(10000);

			}
		} catch (InterruptedException e) {
			// normal
		}

	}



}
