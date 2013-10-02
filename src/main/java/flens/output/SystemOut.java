package flens.output;

import java.io.PrintStream;

import flens.core.Matcher;
import flens.core.Output;
import flens.core.Record;
import flens.output.util.AbstractPumpOutput;

public class SystemOut extends AbstractPumpOutput implements Output {

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
				sent++;
			}
		} catch (InterruptedException e) {
			// break loop
		}

	}
}
