package flens.filter;

import java.util.concurrent.BlockingQueue;

import flens.core.Filter;
import flens.core.Input;
import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;

public abstract class AbstractFilter implements Filter {

	private Tagger tagger;
	private Matcher matcher;
	private String name;

	public AbstractFilter(String name,Tagger tagger, Matcher matcher) {
		this.name = name;
		this.tagger = tagger;
		this.matcher = matcher;
	}

	
	@Override
	public String getName() {
		return name;
	}
	
	protected Record tag(Record r){
		tagger.adapt(r);
		return r;
	}
	
	@Override
	public Matcher getMatcher() {
		return matcher;
	}
	

}
