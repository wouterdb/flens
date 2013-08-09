package flens.filter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

public class JSonDecoder extends AbstractFilter {

	private Gson decoder;

	public JSonDecoder(String name, Tagger tagger, Matcher matcher,int prio) {
		super(name, tagger, matcher,prio);
		decoder = (new GsonBuilder()).serializeSpecialFloatingPointValues().create();
	}

	@Override
	public Collection<Record> process(Record in) {
		//TODO config
		
		
		Map x = decoder.fromJson((String) in.getValues().get("message"),HashMap.class);
		if(x!=null)
			in.getValues().putAll(x);
		tag(in);
		return Collections.EMPTY_LIST;
	}

}
