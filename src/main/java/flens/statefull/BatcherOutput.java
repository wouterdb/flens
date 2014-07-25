package flens.statefull;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import flens.core.Matcher;
import flens.core.Output;
import flens.core.Record;
import flens.input.util.InputQueueExposer;
import flens.output.util.AbstractPumpOutput;

public class BatcherOutput extends AbstractPumpOutput implements Output {

	private InputQueueExposer in;
	private int maxtime;
	private int maxsize;

	private List<Record> buffer = new LinkedList<>();

	public BatcherOutput(String name, String plugin, Matcher matcher,
			int maxtime, int maxsize, InputQueueExposer inpex) {
		super(name, plugin, matcher);
		this.maxtime = maxtime;
		this.maxsize = maxsize;
		this.in = inpex;

	}

	@Override
	public void run() {
		try {
			while (true) {
				Record r = queue.take();
				try {
					process(r);
				} catch (Exception e) {
					err("fault in script", e);
				}
			}
		} catch (InterruptedException e) {
			// normal for stop
			stop();
		}
	}

	public void process(Record in) {
		buffer.add(in);
		if (buffer.size() >= maxsize) {
			transmit();
		} else if (backup == null) {
			startTimer();
		}
	}

	public class TransmitTask extends TimerTask {

		@Override
		public void run() {
			transmit();
		}

	}

	private Timer backup = new Timer("batching filter timer");
	private TransmitTask task = new TransmitTask();

	private void startTimer() {
		backup.schedule(new TransmitTask(), maxtime);

	}

	private synchronized void transmit() {
		task.cancel();
		Record r = Record.pack(buffer);
		buffer = new LinkedList<>();
		in.send(r);
	}

	@Override
	public void stop() {
		backup.cancel();
		super.stop();
	}

}
