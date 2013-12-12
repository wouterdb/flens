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
package flens.core;

import java.util.Map;
import java.util.UUID;

import com.rits.cloning.Cloner;

import dnet.monitor.control.Command;
import dnet.monitor.control.config.ConfigClient;

public class ConfigHandler extends ConfigClient {
	
	public final ConfigParser parser; 

	public ConfigHandler(){
		parser = new ConfigParser(); 
	}
	
	public ConfigHandler(Flengine engine) {
		parser = new ConfigParser(engine);
	}

	@Override
	public void configRemoved(UUID id, Map<String, Object> cfg) {
		System.out.println("removing config: " + id);
		parser.unload(cfg);
		
	}

	@Override
	public void configUpdated(Command command) {
		parser.unload(command.getConfig());
		parser.load(command.getConfig());
	}

	@Override
	public void configAdded(Command command) {
		System.out.println("adding config: " + command);
		parser.load(command.getConfig());
	}

	public Flengine getEngine(){
		return parser.getEngine();
	}

	public void load(Map<String, Object> initial) {
		configs.put(new UUID(0,0),(new Cloner()).deepClone(initial));
		parser.construct(initial);
	}

	@Override
	public String getDeepHelp() {
		return parser.help();
	}

	
}
