package flens.filter;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import flens.core.Filter;
import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

public class Unpacker extends AbstractFilter implements Filter{

	public Unpacker(String name, Tagger tagger, Matcher matcher,int prio) {
		super(name, tagger, matcher,prio);
	}

	@Override
	public Collection<Record> process(Record in) {
		List<Record> outs = new LinkedList<Record>();
		for(String name:in.getValues().keySet()){
			outs.add(makeRecord(in,name,in.getValues().get(name)));
		}
		in.setType(null);
		return outs;
	}

	private Record makeRecord(Record in, String name, Object value) {
		Record out = in.cloneNoValues();
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("metric", name);
		values.put("value", value);
		out.setValues(values);
		return tag(out);
	}

}
