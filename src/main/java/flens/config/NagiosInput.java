package flens.config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import flens.core.Tagger;
import flens.core.Config.Option;
import flens.core.util.AbstractConfig;
import flens.input.OpenTsdbInput;

public class NagiosInput extends AbstractConfig {

	@Override
	protected void construct() {
		if(tagger==Tagger.empty)
			tagger = null;
		Tagger err = readTagger("err-");
		if(err==Tagger.empty)
			err = null;
		String dir = get("dir", "/usr/lib64/nagios/plugins");
		String metric = get("metric", "dns");
		String target = get("target", null);
		List args = getArray("args", Collections.singletonList("8.8.8.8"));
		int interv = getInt("interval", 10000);
			
		engine.addInput(new flens.input.NagiosInput(name, tagger, err, dir,metric,target,args,interv));
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
		out.add(new Option("interval", "int", "10000", "interval between subsequent reports in ms"));
		out.add(new Option("err-add-tags", "[String]","[]", "add following tags to err stream"));
		out.add(new Option("err-remove-tags", "[String]","[]", "remove following tags to err stream"));
		out.add(new Option("err-type", "String", "" ,"type to apply to the records to err stream"));
		out.add(new Option("dir", "String", "/usr/lib64/nagios/plugins" ,"nagios plugin directory"));
		out.add(new Option("metric", "String", "dns" ,"plugin to run"));
		out.add(new Option("args", "String", "" ,"arguments"));
		out.add(new Option("target", "String", "null" ,"target label"));
		return out;
	}


	@Override
	public String getDescription() {
		return "Spawn process and read lines form std.err and std.out";
	}

}

