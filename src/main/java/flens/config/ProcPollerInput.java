package flens.config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import flens.core.Tagger;
import flens.core.Config.Option;
import flens.core.util.AbstractConfig;
import flens.input.OpenTsdbInput;

public class ProcPollerInput extends AbstractConfig {

	@Override
	protected void construct() {
		if(tagger==Tagger.empty)
			tagger = null;
		Tagger err = readTagger("err-");
		if(err==Tagger.empty)
			err = null;
		String cmd = get("cmd", "");
		List args = getArray("args", Collections.EMPTY_LIST);
		int interv = getInt("interval", 10000);
				
		engine.addInput(new flens.input.ProcessPoller(name, tagger, err, cmd,args,interv));
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
		out.add(new Option("cmd", "String", "" ,"command to run"));
		out.add(new Option("args", "String", "" ,"arguments"));
		return out;
	}


	@Override
	public String getDescription() {
		return "Spawn process and read lines form std.err and std.out";
	}

}
