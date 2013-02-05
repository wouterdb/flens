package flens.output;

import java.io.PrintStream;

import flens.core.Matcher;
import flens.core.Output;
import flens.core.Record;
import flens.output.util.StreamPump;

public class SystemOut extends StreamPump implements Output {

	private PrintStream stream;
	
	public SystemOut(String name, Matcher matcher) {
		super(name, matcher);
		stream = System.out;
	}

	public void run() {
		
		try {
			while (running) {
				Record r = queue.take();
				stream.println(String.format("[%s] %s",getName(),r.toLine()));
			}
		} catch (InterruptedException e) {
			// break loop
		}

	}
}
