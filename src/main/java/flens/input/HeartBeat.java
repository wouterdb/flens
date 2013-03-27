package flens.input;

import java.util.Timer;
import java.util.TimerTask;

import flens.core.Constants;
import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AsyncInput;

public class HeartBeat extends AsyncInput {
	
	public class HeartBeatTask extends TimerTask {

		@Override
		public void run() {
			Record r = new Record(null);
			r.getValues().put(Constants.METRIC, "heartbeat");
			dispatch(r);
		}

	}

	protected Timer t;
	protected int interval;

	public HeartBeat(String name, Tagger tagger,int interval) {
		super(name, tagger);
		this.interval = interval;
	}

	@Override
	public void start() {
		t = new Timer(true);
		t.scheduleAtFixedRate(new HeartBeatTask(), 0, interval);
	}

	@Override
	public void stop() {
		t.cancel();
	}

	@Override
	public void join() throws InterruptedException {
		
		
	}

	

}
