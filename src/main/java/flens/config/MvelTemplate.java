package flens.config;

import java.util.LinkedList;
import java.util.List;

import flens.core.Config.Option;
import flens.core.util.AbstractConfig;

public class MvelTemplate extends AbstractConfig{
	
	@Override
	protected boolean isIn() {
		return false;
	}
	
	@Override
	protected void construct() {
		String temp = get("template", null);
		String field = get("field", null);
		engine.addFilter(new flens.filter.MVELTemplate(name,tagger,matcher,prio,field,temp));
	}

	

	@Override
	protected boolean isOut() {
		return false;
	}

	@Override
	public String getDescription() {
		return "run mvel template on fields, place result in field";
	}

	@Override
	public List<Option> getOptions() {
		List<Option>  out = new LinkedList(super.getOptions());
		out.add(new Option("template", "String", " ", "template to execute"));
		out.add(new Option("field", "String", null, "field to place result in"));
		return out;
	}
}
