package flens.input.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.concurrent.BlockingQueue;

import flens.core.Input;
import flens.core.Record;
import flens.core.Tagger;
import flens.core.util.AbstractPlugin;

public class StreamPump extends AbstractActiveInput implements Runnable {

	private BufferedReader reader;

	public StreamPump(String name, Tagger tagger, BufferedReader s) {
		super(name, tagger);
		this.reader = s;
	}

	@Override
	public void run() {
		try {
			while (running) {
				String r = reader.readLine();
				if (r == null)
					running = false;
				else
					dispatch(r);
			}
		} catch (IOException e) {
			err("stream failed", e);
		}
		running = false;
	}

	protected void dispatch(String s) {
		dispatch(new Record(s));
	}

	protected void dispatch(Record r) {
		if (tagger != null)
			super.dispatch(r);
	}
}
