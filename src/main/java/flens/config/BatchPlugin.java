package flens.config;

import java.util.LinkedList;
import java.util.List;

import flens.config.util.ActiveFilter;
import flens.filter.BatchingFilter;
import flens.input.util.InputQueueExposer;

public class BatchPlugin extends ActiveFilter{

	@Override
	public String getDescription() {
		return "plugin packing togheter records in super records";
	}

	@Override
	protected void construct() {
		int interval = getInt("interval", 97);
		int maxbatch = getInt("maxbatch", 100);
	
		InputQueueExposer inpex = new InputQueueExposer(name+"_in",plugin, tagger);
		engine.addInput(inpex);
		
		engine.addOutput(new BatchingFilter(name,plugin,matcher,inpex,maxbatch,interval));
		
	}
	
	public List<Option> getOptions() {
		List<Option>  out = new LinkedList<>(super.getOptions());
		out.add(new Option("interval", "int", "97", "maximal time to wait before a bacth is sent in ms"));
		out.add(new Option("maxbatch", "int", "100", "maximal nr of records per super record"));
		return out;
	}

}
