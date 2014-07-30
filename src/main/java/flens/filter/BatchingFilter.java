package flens.filter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import flens.core.Constants;
import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;
import flens.input.util.InputQueueExposer;
import flens.output.util.AbstractPeriodicOutput;
import flens.output.util.AbstractPumpOutput;

public class BatchingFilter extends AbstractPeriodicOutput {

	public class TransmitTask extends TimerTask {

		@Override
		public void run() {
			transmit();
		}

	}

	private int maxbatch;
	
	private List<Record> buffer = new LinkedList<>();

	public BatchingFilter(String name,String plugin, Matcher matcher, InputQueueExposer inexp, int maxbatch, int maxtime) {
		super(name,plugin, matcher, maxtime,inexp );
		this.maxbatch = maxbatch;
		
	}

	@Override
	public synchronized void process(Record in) {
		buffer.add(in);
		if(buffer.size()>=maxbatch){
			transmit();
		}else if(tt==null){
			restartTimer();
		}
			
	}

	

	private synchronized void transmit() {
		tt.cancel();
		tt=null;
		
		Record r = Record.pack(buffer);
		buffer = new LinkedList<>();
		in.send(r);
	}

	

	@Override
	protected TimerTask createTimerTask() {
		return new TransmitTask();
	}
	
}
