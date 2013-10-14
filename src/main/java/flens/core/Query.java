package flens.core;

import java.util.Map;

import com.google.gson.Gson;

public abstract class Query {
	
	private String query;
	private Map<String,Object> payload;
	

	public abstract void respond(byte[] payload);
	public abstract void respond(String payload);
	
	public void respond(Map<String,Object> payload){
		Gson gson = new Gson();
		respond(gson.toJson(payload));
	}
	
	public void respond(Object payload){
		Gson gson = new Gson();
		respond(gson.toJson(payload));
	}
	
	public abstract void fail(int code, String payload);
		
	public Query(String query, Map<String, Object> payload) {
		super();
		this.query = query;
		this.payload = payload;
	}
	
	public String getQuery() {
		return query;
	}
	
	public Map<String, Object> getPayload() {
		return payload;
	}
	
}
