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

import java.util.LinkedList;
import java.util.List;

import flens.config.util.AbstractConfig;
import flens.input.SelfMonitor;

public class Self extends AbstractConfig{

	@Override
	protected void construct() {
		int interval = getInt("interval", 60000);
		engine.addInput(new SelfMonitor(name,plugin,tagger,engine,interval));
		
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
		out.add(new Option("interval", "int", "60000", "interval (in ms) between subsequent reports"));
		return out;
	}

	@Override
	public String getDescription() {
		return "send out records packed with self diagnostics";
	}

}
