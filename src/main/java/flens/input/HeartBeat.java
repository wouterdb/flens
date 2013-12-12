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

import java.util.Timer;
import java.util.TimerTask;

import flens.core.Constants;
import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractInput;
import flens.input.util.AbstractPeriodicInput;

public class HeartBeat extends AbstractPeriodicInput {
	
	public class HeartBeatTask extends TimerTask {

		@Override
		public void run() {
			Record r = new Record();
			r.getValues().put(Constants.METRIC, "heartbeat");
			dispatch(r);
		}

	}


	public HeartBeat(String name, Tagger tagger,int interval) {
		super(name, tagger,interval);
	}


	@Override
	protected TimerTask getWorker() {
		return new HeartBeatTask();
	}


	

}
