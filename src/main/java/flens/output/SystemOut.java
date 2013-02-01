package flens.output;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import flens.core.Constants;
import flens.core.Matcher;
import flens.core.Output;
import flens.core.Record;
import flens.output.util.StreamOutPump;

public class SystemOut implements Output {

	public final Matcher matcher = Constants.ALLMATCHER;
	private BlockingQueue<Record> queue = new LinkedBlockingQueue<Record>();
	private StreamOutPump worker; 
	
	public SystemOut() {
		worker = new StreamOutPump(queue,System.out,getName());
	}
	
	public Matcher getMatcher() {
		return matcher;
	}

	public String getName() {
		return "sys-out";
	}

	public Queue<Record> getOutputQueue() {
		return queue ;
	}

	public void start() {
		worker.start();

	}

	public void stop() {
		worker.stop();

	}

}
