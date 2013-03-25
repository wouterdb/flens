package flens.config;

import java.util.LinkedList;
import java.util.List;

import flens.core.Config.Option;
import flens.core.util.AbstractConfig;
import flens.input.SelfMonitor;

public class Self extends AbstractConfig{

	@Override
	protected void construct() {
		int interval = getInt("interval", 60000);
		engine.addInput(new SelfMonitor(name,tagger,engine,interval));
		
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
		out.add(new Option("interval", "int", "60000", "interval (in ms) between subsequent reports"));
		return out;
	}

	@Override
	public String getDescription() {
		return "send out records packed with self diagnostics";
	}

}
