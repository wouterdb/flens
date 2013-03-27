package flens.filter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

public class JsonEncoder extends AbstractFilter {

	private Gson decoder;
	private String field;
	private List<String> fields;

	public JsonEncoder(String name, Tagger tagger, Matcher matcher,String field, List<String> fieldToInclude) {
		super(name, tagger, matcher);
		decoder = new Gson();
		this.field = field;
		this.fields = fieldToInclude;
	}

	@Override
	public Collection<Record> process(Record in) {
		Map<String, Object> out = in.getValues();
		if(fields!=null){
			out = new HashMap<String,Object>();
			for(String key:fields){
				out.put(key, in.getValues().get(key));
			}
		}
		
		String json = decoder.toJson(out);
		
		in.getValues().put(field, json);
		tag(in);
		return Collections.EMPTY_LIST;
	}

}
