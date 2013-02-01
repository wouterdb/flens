package flens.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import flens.core.Input;
import flens.core.Record;
import flens.core.Tagger;

public class SystemIn extends AbstractInput {

	private BufferedReader inr;

	public SystemIn(String name,Tagger tagger) {
		super(name,tagger);
	}

	public void start() {
		this.inr = new BufferedReader(new InputStreamReader(System.in));
		super.start();
	}

	public void stop() {
		super.stop();

		try {
			inr.close();
		} catch (IOException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"cloud not close", e);
		}

	}

	public void run() {
		try {
			while (running) {
				String line;

				line = inr.readLine();

				in.add(new Record("sys.in",  line));
			}

		} catch (IOException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"system in failed", e);
		}
	}

}
