package flens.config;

import java.util.LinkedList;
import java.util.List;

import flens.config.util.ActiveFilter;
import flens.core.Config.Option;
import flens.input.util.InputQueueExposer;
import flens.statefull.AlertOutput;
import flens.statefull.BatcherOutput;

public class Batcher extends ActiveFilter {

	@Override
	public String getDescription() {
		return "plugin collecting records for batch transmission";
	}

	@Override
	protected void construct() {
		int interval = getInt("maxtime", 1000);
		int size = getInt("maxsize", 200);
			
		InputQueueExposer inpex = new InputQueueExposer(name+"_in",plugin, tagger);
		engine.addInput(inpex);
		
		engine.addOutput(new BatcherOutput(name,plugin,matcher,interval, size,inpex));
		
	}
	
	public List<Option> getOptions() {
		List<Option>  out = new LinkedList<>(super.getOptions());
		out.add(new Option("maxtime", "int", "1000", "maximal time to wait before batch is sent"));
		out.add(new Option("maxsize", "int", "1000", "max number of events batched toghetter"));
		return out;
	}


}
