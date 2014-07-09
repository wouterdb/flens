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

import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;

import flens.config.util.AbstractConfig;

public class HttpPollerInput extends AbstractConfig {

	@Override
	protected void construct() {
	
		int interv = getInt("interval", 10000);
		String url = get("url","");
		try {
			engine.addInput(new flens.input.HttpPullInput(name,plugin, tagger, interv, url));
		} catch (MalformedURLException e) {
			warn("plugin failed, bad url");
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
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
		out.add(new Option("url", "String", "" ,"the url"));
		return out;
	}


	@Override
	public String getDescription() {
		return "poll http, via get request";
	}

}
