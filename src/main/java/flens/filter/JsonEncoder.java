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
package flens.filter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

public class JsonEncoder extends AbstractFilter {

	private Gson decoder;
	private String field;
	private List<String> fields;
	private List<String> exfields;

	public JsonEncoder(String name, String plugin, Tagger tagger, Matcher matcher,int prio,String field, List<String> fieldToInclude,List<String> fieldsToExclude) {
		super(name, plugin, tagger, matcher,prio);
		decoder = (new GsonBuilder()).serializeSpecialFloatingPointValues().create();
		this.field = field;
		this.fields = fieldToInclude;
		this.exfields = fieldsToExclude;
	}

	@Override
	public Collection<Record> process(Record in) {
		Map<String, Object> out = in.getValues();
		if(fields!=null){
			out = new HashMap<String,Object>();
			for(String key:fields){
				out.put(key, in.getValues().get(key));
			}
		}else if(exfields!=null){
			out = new HashMap<>(out);
			for(String key:exfields){
				out.remove(key);
			}
		}
		
		String json = decoder.toJson(out);
		
		in.getValues().put(field, json);
		tag(in);
		return Collections.emptyList();
	}
}
