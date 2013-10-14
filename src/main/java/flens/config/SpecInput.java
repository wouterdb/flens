package flens.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import flens.core.util.AbstractConfig;
import flens.input.OpenTsdbInput;

public class SpecInput extends AbstractConfig {

	@Override
	protected void construct() {

		int interval = getInt("interval",10000);
		List<String> specs = getArray("tests", Arrays.asList("write","read","cpu","exec","sleep"));
		
		engine.addInput(new flens.input.SpecInput(name,tagger,interval,specs));
	}

	
	@Override
	protected boolean isIn() {
		return true;
	}

	@Override
	protected boolean isOut() {
		return false;
	}
	
	@Override
	public List<Option> getOptions() {
		List<Option>  out = new LinkedList(super.getOptions());
		out.add(new Option("interval", "int", "10", "interval between specs tests in ms"));
		out.add(new Option("tests", "[String]", "[\"disk\"]", "test suites to run"));

		return out;
	}


	@Override
	public String getDescription() {
		return "Runs small tests to estimate machine performance";
	}

}
