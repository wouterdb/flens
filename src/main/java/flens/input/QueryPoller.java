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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import com.google.gson.Gson;

import flens.core.Flengine;
import flens.core.Query;
import flens.core.QueryHandler;
import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractPeriodicInput;

public class QueryPoller extends AbstractPeriodicInput {

	@SuppressWarnings("unchecked")
	public QueryPoller(Flengine engine, String name, String plugin, Tagger tagger, int interval,String query, String metric) {
		super(name,plugin, tagger, interval);
		this.engine = engine;
		this.metric = metric;
		this.myquery = new QueryToRecordPump(query, Collections.EMPTY_MAP);
	}

	private Flengine engine;
	private Query myquery;
	private String metric;

	@Override
	protected TimerTask getWorker() {
		return new TimerTask() {

			@Override
			public void run() {
				List<QueryHandler> qhs = engine.getHandler(myquery);
				for (QueryHandler qh : qhs) {
					qh.handle(myquery);
				}
			}
		};
	}
	
	private class QueryToRecordPump extends Query{

		public QueryToRecordPump(String query, Map<String, Object> payload) {
			super(query, payload);
		}

		@Override
		public void respond(byte[] payload) {
			dispatch(Record.createWithValue(metric,payload));		
		}

		@Override
		public void respond(String payload) {
			dispatch(Record.createWithValue(metric,payload));		
		}

		public void respond(Map<String,Object> payload){
			dispatch(Record.createWithValues(payload));
		}
		
		//FIXME: better way to pack objects
		public void respond(Object payload){
			Gson gson = new Gson();
			respond(gson.toJson(payload));
		}
		
		@Override
		public void fail(int code, String payload) {
			warn("failed to execute "+ getQuery() + " : "+ payload);
		}
		
	}

}
