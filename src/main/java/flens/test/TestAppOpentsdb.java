package flens.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import flens.core.Flengine;
import flens.input.OpenTsdbInput;
import flens.input.SelfMonitor;
import flens.input.SystemIn;
import flens.output.SystemOut;

public class TestAppOpentsdb {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		Logger.getGlobal().setLevel(Level.FINEST);
		Flengine engine = new Flengine();
		engine.addInput(new OpenTsdbInput());
		engine.addInput(new SelfMonitor(engine));
		engine.addOutput(new SystemOut());
		engine.start();
		
	}

}
