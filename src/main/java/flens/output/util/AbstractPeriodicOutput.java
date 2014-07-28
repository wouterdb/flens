package flens.output.util;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import org.mvel2.MVEL;

import flens.core.Matcher;
import flens.core.Record;
import flens.input.util.InputQueueExposer;
import flens.util.MVELUtil;

public abstract class AbstractPeriodicOutput extends AbstractPumpOutput {
	

	private int interval;
	protected InputQueueExposer in;
	private Timer t = new Timer();
	protected TimerTask tt;

	public AbstractPeriodicOutput(String name,String plugin, Matcher matcher, int interval, InputQueueExposer inpex) {
		super(name,plugin, matcher);
		this.interval = interval;
		this.in = inpex;
	}

	@Override
	public void start() {
		super.start();
		restartTimer();
	}
	
	public synchronized void restartTimer(){
		if(tt!=null)
			tt.cancel();
		tt = createTimerTask();
		t.schedule(tt, interval,interval);
	}

	protected abstract TimerTask createTimerTask();

	@Override
	public void run() {
		try {
			while (true) {
				Record r = queue.take();
				try{
					process(r);
				}catch(Exception e){
					err("fault in script",e);
				}
			}
		} catch (InterruptedException e) {
			// normal for stop
			stop();
		}
	}

	protected abstract void process(Record in);
}
