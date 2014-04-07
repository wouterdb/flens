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
package flens.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import flens.config.util.Reflector;
import flens.core.Flengine;

public abstract class AbstractPlugin {
	
	public void warn(String line) {
		Logger.getLogger(getClass().getName()).log(Level.WARNING,
				getName() + ": " + line  + "");
		
	}
	
	protected void err(String msg, Throwable e) {
		Logger.getLogger(getClass().getName()).log(Level.SEVERE, msg,e);
	}

	protected void warn(String msg, Exception e) {
		Logger.getLogger(getClass().getName()).log(Level.WARNING, msg,e);
		
	}
	
	protected void info(String msg) {
		Logger.getLogger(getClass().getName()).log(Level.INFO, msg);
		
	}

	public abstract String getName();
	public abstract String getPlugin();
	
	public void writeConfig(Flengine engine, Map<String,Object> tree){
		Map<String,Object> subtree = new HashMap<String, Object>();
		tree.put(getPlugin(),subtree);
		Reflector.store(this, engine.getPluginRepo().get(getPlugin()));
	}

}
