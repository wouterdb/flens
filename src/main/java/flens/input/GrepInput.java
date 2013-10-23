package flens.input;

import java.io.File;
import java.util.regex.Pattern;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractActiveInput;

public class GrepInput extends AbstractActiveInput implements TailerListener{

	private Tailer tailer;
	private Pattern regex;
	private boolean tailFromEnd;
	private long delay = 1000;

	public GrepInput(String name, Tagger tagger, String file, String regex,boolean tail) {
		super(name, tagger);
		tailer = new Tailer(new File(file), this,delay ,tail);
		this.regex = Pattern.compile(regex); 
		this.tailFromEnd=tail;
	}

	@Override
	public void run() {
		tailer.run();
	}

	@Override
	public void stop() {
		tailer.stop();
		super.stop();
	}

	@Override
	public void init(Tailer tailer) {
	}

	@Override
	public void fileNotFound() {
		err("file not found: " + tailer.getFile().getName(),null);
	}

	@Override
	public void fileRotated() {
		
		
	}

	@Override
	public void handle(String line) {
		if(regex.matcher(line).matches())
			dispatch(new Record(line));
		
	}

	@Override
	public void handle(Exception ex) {
		err("tailer failed: " + tailer.getFile().getName(),ex);
		
	}
}
