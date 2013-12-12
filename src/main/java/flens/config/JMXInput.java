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

import flens.core.util.AbstractConfig;
import flens.input.OpenTsdbInput;

public class JMXInput extends AbstractConfig {

	@Override
	protected void construct() {

		List<String> domain = getArray("domains",Collections.EMPTY_LIST);
		int interval = getInt("interval",10000);
		int multiplier = getInt("vm-intervals",10);
		String jvmSelector = get("jvm", ".*");
		
		engine.addInput(new flens.input.JMXInput(name,tagger,jvmSelector,domain,interval,multiplier));
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
		out.add(new Option("vm-intervals", "int", "10", "search for new VM's every vm-intervals intervals"));
		out.add(new Option("jvm", "String", ".*", "pid of vm or regex on vm name"));
		out.add(new Option("domains", "List", "[]", "jmx domains to report on"));
		return out;
	}


	@Override
	public String getDescription() {
		return "Regualarly searches JMX for metrics, jmx key-value metrics become field-values in flens ";
	}

}
