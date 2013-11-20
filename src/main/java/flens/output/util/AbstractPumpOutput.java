package flens.output.util;

import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import flens.core.Matcher;
import flens.core.Output;
import flens.core.Record;
import flens.core.util.AbstractPlugin;

public abstract class AbstractPumpOutput extends AbstractPlugin implements
		Output, Runnable {

	public AbstractPumpOutput(String name, Matcher matcher) {
		super();
		this.name = name;
		this.matcher = matcher;
	}

	private String name;
	private Matcher matcher;
	protected BlockingQueue<Record> queue = new LinkedBlockingQueue<>();
	private Thread thread;
	protected volatile boolean running;
	protected volatile boolean reconnecting;

	protected int reconnectDelay = 10000;
	protected int flushOnSize = 10000;
	protected int sent;
	protected int lost;

	@Override
	public Matcher getMatcher() {
		return matcher;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Queue<Record> getOutputQueue() {
		return queue;
	}

	@Override
	public void start() {
		thread = new Thread(this);
		reconnecting = false;
		running = true;
		thread.start();
	}

	@Override
	public void stop() {
		running = false;
		thread.interrupt();
	}

	protected synchronized void reconnect() {
		// re-entrant
		if (reconnecting)
			return;
		reconnecting = true;
		// FIXME:may lose records
		Timer t = new Timer();
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				if (flushOnSize > 0 && getOutputQueue().size() > flushOnSize) {
					lost += getOutputQueue().size();
					getOutputQueue().clear();
					warn("flushing queue to prevent overflow: " + getName());
				}
				try {
					start();
				} catch (Exception e) {
					err("reconnect failed", e);
					reconnect();
				}
			}
		}, reconnectDelay);

	}

	@Override
	public int getRecordsLost() {
		return lost;
	}

	@Override
	public int getRecordsSent() {
		return sent;
	}

	protected byte[] getBytes(Object raw) {
		byte[] body;

		if (raw instanceof byte[]) {
			body = (byte[]) raw;
		} else if (raw instanceof String) {
			try {
				body = ((String) raw).getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				err("could not use utf-8!", e);
				body = ((String) raw).getBytes();
			}
		} else {
			try {
				body = raw.toString().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				err("could not use utf-8!", e);
				body = raw.toString().getBytes();
			}
		}

		return body;
	}
}
