package flens.config;

import java.util.LinkedList;
import java.util.List;

import flens.config.util.ActiveFilter;
import flens.input.util.InputQueueExposer;
import flens.output.AlertOutput;

public class AlertPlugin extends ActiveFilter{

	@Override
	public String getDescription() {
		return "plugin listening for event, raising alert if not match is made for a certain time";
	}

	@Override
	protected void construct() {
		int interval = getInt("interval", 12000);
		String script = get("script","return true;") ;
		String msg = get("msg","shots fired") ;
	
		InputQueueExposer inpex = new InputQueueExposer(name+"_in",plugin, tagger);
		engine.addInput(inpex);
		
		engine.addOutput(new AlertOutput(name,plugin,matcher,interval, script,msg,inpex));
		
	}
	
	public List<Option> getOptions() {
		List<Option>  out = new LinkedList<>(super.getOptions());
		out.add(new Option("interval", "int", "12000", "time to wait before alert is raised in ms"));
		out.add(new Option("script", "String", "return true;", "mvel script to check is record matches"));
		out.add(new Option("msg", "String", "shots fired", "alert body"));
		return out;
	}

}
