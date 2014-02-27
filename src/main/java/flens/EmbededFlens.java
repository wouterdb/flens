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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import flens.core.ConfigBuilder;
import flens.core.Flengine;

public class EmbededFlens {
	
	
	
	private Flengine engine;

	public EmbededFlens() {
		
		Reader s = getConfig();
		if(s==null)
			return;
		ConfigBuilder cb = new ConfigBuilder(s);
		cb.run();
		this.engine=cb.getEngine();
	}

	public EmbededFlens(Flengine engine) {
		this.engine=engine;
	}

	private Reader getConfig() {
		String prop = System.getProperty("flens.config");
		if(prop==null)
			return null;
		try {
			return new FileReader(prop);
		} catch (FileNotFoundException e) {
			//fixme: log decently
			e.printStackTrace();
			return null;
		}
	}
	
	public Flengine getEngine() {
		return engine;
	}
	
	
	private static EmbededFlens instance;
	
	public static EmbededFlens getInstance(){
		if(instance != null)
			return instance;
		synchronized(EmbededFlens.class){
			if(instance != null)
				return instance;
			instance = new EmbededFlens();
			if(instance.engine!=null)
				instance.engine.start();
		}
		return instance;
	}

	public static void setInstance(Flengine engine) {
		instance = new EmbededFlens(engine);
	}

}
