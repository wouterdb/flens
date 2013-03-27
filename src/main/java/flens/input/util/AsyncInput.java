package flens.input.util;

import java.util.concurrent.BlockingQueue;

import flens.core.Input;
import flens.core.Record;
import flens.core.Tagger;
import flens.core.util.AbstractPlugin;

public abstract class AsyncInput extends AbstractPlugin implements Input{

	protected BlockingQueue<Record> in;
	private Tagger tagger;
	private String name;

	public AsyncInput(String name,Tagger tagger) {
		this.name = name;
		this.tagger = tagger;
	}

	
	@Override
	public String getName() {
		return name;
	}
	
	protected void dispatch(Record r){
		tagger.adapt(r);
		in.add(r);
	}
	
	public void setInputQueue(BlockingQueue<Record> queue) {
		this.in = queue;
	}
}
