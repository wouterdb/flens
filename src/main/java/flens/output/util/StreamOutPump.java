package flens.output.util;

import java.io.PrintStream;
import java.util.concurrent.BlockingQueue;

import flens.core.Record;

public class StreamOutPump implements Runnable {

	private BlockingQueue<Record> q;
	private PrintStream stream;
	private Thread worker;
	private volatile boolean running;
	private String name;

	public StreamOutPump(BlockingQueue<Record> queue, PrintStream out,String name) {
		this.q = queue;
		this.stream = out;
		this.name = name;
	}

	public void start() {
		worker = new Thread(this);
		running = true;
		worker.start();
	}

	public void stop() {
		running = false;
		worker.interrupt();
	}

	public void run() {
		try {
			while (running) {
				Record r = q.take();
				stream.println(String.format("[%s] %s",name,r.toLine()));
			}
		} catch (InterruptedException e) {
			// break loop
		}

	}

}
