package flens.input.util;

import java.util.concurrent.BlockingQueue;

import flens.core.Input;
import flens.core.Record;
import flens.core.Tagger;
import flens.core.util.AbstractPlugin;

public abstract class AbstractInput extends AbstractPlugin implements Input{

	protected BlockingQueue<Record> in;
	protected Tagger tagger;
	private String name;
	private int sent;

	public AbstractInput(String name,Tagger tagger) {
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
		sent++;
	}
	
	public void setInputQueue(BlockingQueue<Record> queue) {
		this.in = queue;
	}
	
	public int getRecordsSent() {
		return sent;
	}
}
