package flens.config;

import java.util.LinkedList;
import java.util.List;

import flens.core.Config.Option;
import flens.core.util.AbstractConfig;

public class PythonFilter extends AbstractConfig{
	
	@Override
	protected boolean isIn() {
		return false;
	}
	
	@Override
	protected void construct() {
		String s = get("script", "");
		engine.addFilter(new flens.filter.JythonFilter(name,tagger,matcher,prio,s));
	}

	

	@Override
	protected boolean isOut() {
		return false;
	}

	@Override
	public String getDescription() {
		return "run mvel on fields";
	}

	@Override
	public List<Option> getOptions() {
		List<Option>  out = new LinkedList(super.getOptions());
		out.add(new Option("script", "String", "", "script to execute"));
		return out;
	}
}
