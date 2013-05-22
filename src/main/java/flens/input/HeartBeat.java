package flens.input;

import java.util.Timer;
import java.util.TimerTask;

import flens.core.Constants;
import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractInput;
import flens.input.util.AbstractPeriodicInput;

public class HeartBeat extends AbstractPeriodicInput {
	
	public class HeartBeatTask extends TimerTask {

		@Override
		public void run() {
			Record r = new Record();
			r.getValues().put(Constants.METRIC, "heartbeat");
			dispatch(r);
		}

	}


	public HeartBeat(String name, Tagger tagger,int interval) {
		super(name, tagger,interval);
	}


	@Override
	protected TimerTask getWorker() {
		return new HeartBeatTask();
	}


	

}
