package flens.config;

import java.util.LinkedList;
import java.util.List;

import flens.core.Config.Option;
import flens.core.util.AbstractConfig;

public class GrepIn extends AbstractConfig{

	@Override
	protected boolean isIn() {
		return true;
	}

	@Override
	protected void construct() {
		String file = get("file", null);
		String regex = get("regex",".*");
		
		engine.addInput(new flens.input.GrepInput(name,tagger,file,regex));
	}

	@Override
	protected boolean isOut() {
		return false;
	}

	@Override
	public List<Option> getOptions() {
		List<Option>  out = new LinkedList(super.getOptions());
		out.add(new Option("file", "String", "", "file to tail"));
		out.add(new Option("regex", "String", ".*", "regex to filter results"));
		return out;
	}

	@Override
	public String getDescription() {
		return "log tailer with regex support";
	}

}