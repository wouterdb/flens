package flens.input;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import javax.crypto.spec.OAEPParameterSpec;

import com.google.gson.Gson;

import flens.core.Flengine;
import flens.core.Query;
import flens.core.QueryHandler;
import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractPeriodicInput;

public class QueryPoller extends AbstractPeriodicInput {

	public QueryPoller(Flengine engine, String name, Tagger tagger, int interval,String query, String metric) {
		super(name, tagger, interval);
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
