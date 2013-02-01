package flens.test;

import flens.core.Flengine;
import flens.input.SelfMonitor;
import flens.input.SystemIn;
import flens.output.SystemOut;

public class TestApp1 {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		Flengine engine = new Flengine();
		engine.addInput(new SystemIn());
		engine.addInput(new SelfMonitor(engine));
		engine.addOutput(new SystemOut());
		engine.start();
		
	}

}
