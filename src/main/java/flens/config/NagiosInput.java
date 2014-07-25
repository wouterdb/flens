/**
 *
 *     Copyright 2013 KU Leuven Research and Development - iMinds - Distrinet
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     Administrative Contact: dnet-project-office@cs.kuleuven.be
 *     Technical Contact: wouter.deborger@cs.kuleuven.be
 */
package flens.config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import flens.config.util.AbstractConfig;
import flens.core.Tagger;

public class NagiosInput extends AbstractConfig {

	@Override
	@SuppressWarnings("unchecked")
	protected void construct() {
		if(tagger==Tagger.empty)
			tagger = null;
		Tagger err = readTagger("err-");
		if(err==Tagger.empty)
			err = null;
		String dir = get("dir", "/usr/lib64/nagios/plugins");
		String metric = get("metric", "dns");
		String target = get("target", null);
		List<String> args = getArray("args", Collections.singletonList("8.8.8.8"));
		int interv = getInt("interval", 10000);
			
		engine.addInput(new flens.input.NagiosInput(name,plugin, tagger, err, dir,metric,target,args,interv));
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
		List<Option>  out = new LinkedList<Option>(super.getOptions());
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

