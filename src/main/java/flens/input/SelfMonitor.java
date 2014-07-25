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
package flens.input;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import flens.core.Flengine;
import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractActiveInput;

//TODO make timer/executor based
public class SelfMonitor extends AbstractActiveInput{
	
	public static final String type = "flens";
	
	private Flengine engine;

	private long interval;

	public SelfMonitor(String name,String plugin, Tagger tagger, Flengine e, int interval) {
		super(name,plugin, tagger);
		this.engine = e;
		this.interval = interval;
	}

	// TODO make configurable
	public void run() {
		try {
			while (running) {
				Set<Record> out = new HashSet<>();
				engine.report(out);
				for (Record r : out) {
					dispatch(r);
				}
				Thread.sleep(interval);

			}
		} catch (InterruptedException e) {
			// normal
		}

	}

	@Override
	public boolean canUpdateConfig() {
		return false;
	}

	@Override
	public void updateConfig(Flengine engine, Map<String, Object> tree) {
		throw new UnsupportedOperationException();
		
	}

}
