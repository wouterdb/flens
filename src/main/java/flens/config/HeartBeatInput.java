package flens.config;

import java.util.LinkedList;
import java.util.List;

import flens.core.util.AbstractConfig;
import flens.input.OpenTsdbInput;

public class HeartBeatInput extends AbstractConfig {

	@Override
	protected void construct() {

		int interval = getInt("interval",10000);
		
		engine.addInput(new flens.input.HeartBeat(name,tagger,interval));
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
		out.add(new Option("interval", "int", "10", "interval between heartbeats"));
		
		return out;
	}


	@Override
	public String getDescription() {
		return "Regualarly sends packets";
	}

}
