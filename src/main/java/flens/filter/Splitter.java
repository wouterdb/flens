package flens.filter;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import flens.core.Filter;
import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

public class Splitter extends AbstractFilter implements Filter{
	
	List<String> keys = new LinkedList<>();

	public Splitter(String name, Tagger tagger, Matcher matcher,int prio, List<String> keys) {
		super(name, tagger, matcher,prio);
		this.keys = keys;
	}

	@Override
	public Collection<Record> process(Record in) {
		List<Record> outs = new LinkedList<Record>();
		
		Map<String,Object> pairs = new HashMap<>();
		
		
		for(String name:keys){
			pairs.put(name,in.getValues().remove(name));
		}
		
		for(Map.Entry<String, Object> entries:pairs.entrySet()){
			outs.add(makeRecord(in, entries.getKey(), entries.getValue()));
		}
		//kill record
		in.setType(null);
		return outs;
	}

	private Record makeRecord(Record in, String name, Object value) {
		Record out = in.doClone();
		Map<String, Object> values = out.getValues();
		values.put("metric", name);
		values.put("value", value);
		return tag(out);
	}

}
