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

import flens.core.Tagger;
import flens.core.Config.Option;
import flens.core.util.AbstractConfig;
import flens.input.OpenTsdbInput;

public class ProcInput extends AbstractConfig {

	@Override
	protected void construct() {
		if(tagger==Tagger.empty)
			tagger = null;
		Tagger err = readTagger("err-");
		if(err==Tagger.empty)
			err = null;
		String cmd = get("cmd", "");
		List args = getArray("args", Collections.EMPTY_LIST);
		engine.addInput(new flens.input.ProcessTailer(name, tagger, err, cmd,args));
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
