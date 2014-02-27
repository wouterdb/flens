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
package flens;

import static flens.core.util.ConfigUtil.collectConfig;

import java.io.IOException;
import java.util.Map;

import flens.core.ConfigHandler;
import flens.core.ConfigParser;
import flens.core.Util;

public class Service {

	public static void main(String[] args) throws IOException {

		ConfigParser ch = new ConfigParser();
		
		Map<String, Object> myconfig = collectConfig(args[0]);

		Map<String, String> tags = (Map<String, String>) myconfig.get("tags");

		Map<String, Object> initial = (Map<String, Object>) myconfig
				.get("init");

		String name = (String) myconfig.get("name");
		if(name != null)
			Util.overriderHostname(name);

		if(initial!=null)
			ch.construct(initial);
		
		if(tags!=null)
			ch.getEngine().addTags(tags);
		
		ch.getEngine().start();
		
	}



}
