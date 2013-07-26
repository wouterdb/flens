package flens.config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import flens.core.Config.Option;
import flens.core.util.AbstractConfig;

public class Splitter extends AbstractConfig{
	
	@Override
	protected boolean isIn() {
		return false;
	}
	
	@Override
	protected void construct() {
		List<String> temp = getArray("fields",Collections.EMPTY_LIST);
		engine.addFilter(new flens.filter.Splitter(name,tagger,matcher,prio,temp));
	}

	

	@Override
	protected boolean isOut() {
		return false;
	}

	@Override
	public String getDescription() {
		return "split of certain k-v pairs as new records, retaining all other fields";
	}

	@Override
	public List<Option> getOptions() {
		List<Option>  out = new LinkedList(super.getOptions());
		out.add(new Option("fiels", "String", "[]", "fields to become new metrics"));
		return out;
	}
}
