package flens.config;

import java.util.LinkedList;
import java.util.List;

import flens.core.Config.Option;
import flens.core.util.AbstractConfig;

public class JsonEncoder extends AbstractConfig{

	@Override
	protected boolean isIn() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void construct() {
		String field = get("field", "json");
		List<String> fields = getArray("fields", null);
		List<String> exfields = getArray("exclude-fields", null);
		
		engine.addFilter(new flens.filter.JsonEncoder(name,tagger,matcher,prio,field,fields,exfields));
	}

	@Override
	protected boolean isOut() {
		return false;
	}

	@Override
	public String getDescription() {
		return "encode json messages";
	}

	@Override
	public List<Option> getOptions() {
		List<Option>  out = new LinkedList(super.getOptions());
		out.add(new Option("field", "String", "json", "field in which to place json encode record"));
		out.add(new Option("fields", "[String]", "null", "fields in record to include in json, if empty use excludes"));
		out.add(new Option("exclude-fields", "[String]", "null", "fields to ignore when forming json, disabled by include"));
		return out;
	}
}
