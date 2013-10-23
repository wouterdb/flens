package flens.filter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

public class JsonEncoder extends AbstractFilter {

	private Gson decoder;
	private String field;
	private List<String> fields;
	private List<String> exfields;

	public JsonEncoder(String name, Tagger tagger, Matcher matcher,int prio,String field, List<String> fieldToInclude,List<String> fieldsToExclude) {
		super(name, tagger, matcher,prio);
		decoder = (new GsonBuilder()).serializeSpecialFloatingPointValues().create();
		this.field = field;
		this.fields = fieldToInclude;
		this.exfields = fieldsToExclude;
	}

	@Override
	public Collection<Record> process(Record in) {
		Map<String, Object> out = in.getValues();
		if(fields!=null){
			out = new HashMap<String,Object>();
			for(String key:fields){
				out.put(key, in.getValues().get(key));
			}
		}else if(exfields!=null){
			out = new HashMap<>(out);
			for(String key:exfields){
				out.remove(key);
			}
		}
		
		String json = decoder.toJson(out);
		
		in.getValues().put(field, json);
		tag(in);
		return Collections.EMPTY_LIST;
	}
}
