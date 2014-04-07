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
import flens.core.Config.Option;

public class Splitter extends AbstractConfig{
	
	@Override
	protected boolean isIn() {
		return false;
	}
	
	@Override
	protected void construct() {
		List<String> temp = getArray("fields",Collections.EMPTY_LIST);
		engine.addFilter(new flens.filter.Splitter(name,plugin,tagger,matcher,prio,temp));
	}

	

	@Override
	protected boolean isOut() {
		return false;
	}

	@Override
	public String getDescription() {
		return "split of certain k-v pairs as new records, retaining all other fields";
	}

	@Override
	public List<Option> getOptions() {
		List<Option>  out = new LinkedList(super.getOptions());
		out.add(new Option("fiels", "String", "[]", "fields to become new metrics"));
		return out;
	}
}
