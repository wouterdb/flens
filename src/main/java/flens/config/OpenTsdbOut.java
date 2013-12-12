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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import flens.core.Config.Option;
import flens.core.util.AbstractConfig;
import flens.output.OpenTsdbOutput;

public class OpenTsdbOut extends AbstractConfig{
	
	

	@Override
	protected boolean isIn() {
		
		return false;
	}
	
	@Override
	protected void construct() {
		String host = get("host", "localhost");
		int port = getInt("port", 4242);
		List<String> stags = getArray("send-tags",new LinkedList<String>());
		engine.addOutput(new OpenTsdbOutput(name,matcher,host,port,(stags)));
	}

	@Override
	protected boolean isOut() {
		return true;
	}
	
	@Override
	public List<Option> getOptions() {
		List<Option>  out = new LinkedList(super.getOptions());
		out.add(new Option("port", "int", "4242", "port to which to connect"));
		out.add(new Option("host", "String", "localhost", "host to which to connect"));
		out.add(new Option("send-tags", "[String]", "[]", "field to append as tags, use [fliedname:tag-name] to rename"));
		return out;
	}
	
	@Override
	public String getDescription() {
		return "send out records to an opentsdb server \n send out messages of the form \n put ${metric} ${timestamp/1000} ${value} host=${source}";
	}

}
