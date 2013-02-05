package flens.output.util;

import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import flens.core.Matcher;
import flens.core.Output;
import flens.core.Record;

public abstract class StreamPump implements Output, Runnable {
	
	public StreamPump(String name, Matcher matcher) {
		super();
		this.name = name;
		this.matcher = matcher;
	}

	private String name;
	private Matcher matcher;
	protected BlockingQueue<Record> queue = new LinkedBlockingQueue<>();
	private Thread thread;
	protected volatile boolean running;

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
		return queue ;
	}

	@Override
	public void start() {
		thread = new Thread(this);
		running = true;
		thread.start();
	}

	@Override
	public void stop() {
		running = false;
		thread.interrupt();
	}

}
