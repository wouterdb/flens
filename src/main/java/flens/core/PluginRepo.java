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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import flens.core.Config.Option;

public class PluginRepo {

	private Gson gson = new Gson();
	private Map<String,String> raw ;
	private Map<String,Config> processed = new HashMap<String, Config>() ;
	
	public PluginRepo(InputStream in) {
		this.raw = gson.fromJson(new InputStreamReader(in), HashMap.class);
	
	}

	

	
	public Config get(String key) {
		if(processed.containsKey(key)){
			return processed.get(key);
		}
		String clazz = raw.get(key);
		
		
		Config x;
		try {
			x = (Config) getClass().getClassLoader().loadClass(clazz).newInstance();
			processed.put(key, x);
			
			return x;
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.INFO,"plugin not found",e);
			return null;
		} 
		
		
	}

	public String help() {
		StringBuilder help = new StringBuilder();
		for(String key:raw.keySet()){
				Config x = get(key);
				if(x!=null){
					help.append(makeHelp(key,x));
				}
		}
		return help.toString();
	}




	private String makeHelp(String key, Config x) {
		StringBuilder sb = new StringBuilder();
		sb.append(key);
		sb.append(" ");
		sb.append(x.getDescription().replaceAll("\n", " "));
		sb.append("\n");
		for(Option opt:x.getOptions()){
			sb.append("\t");
			sb.append(opt.getName());
			sb.append("\t");
			sb.append(opt.getType());
			sb.append("\t");
			sb.append(opt.getDefaultv());
			sb.append("\t");
			sb.append(opt.getDescr());
			sb.append("\n");
		}
		return sb.toString();
	}





}
