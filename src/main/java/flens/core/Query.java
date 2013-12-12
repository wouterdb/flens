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
