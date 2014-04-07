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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.mvel2.MVEL;

import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

public class MVELFilter extends AbstractFilter{

	private String script;
	private Serializable compiled;


	public MVELFilter(String name,String plugin, Tagger tagger, Matcher matcher,int prio,String script) {
		super(name, plugin, tagger, matcher,prio);
		this.script = script;
		start();
	}
	
	

	private void start() {
		 // Compile the expression.
		compiled = MVEL.compileExpression(script);
	}



	@Override
	public Collection<Record> process(Record in) {
		in.getValues().put("DISCARD", false);
		Object records = MVEL.executeExpression(compiled, in.getValues());
		
		tag(in);
		
		Object d = in.getValues().remove("DISCARD");
		if(d instanceof Boolean && ((Boolean)d))
			in.setType(null);
		
		if(records == null)
			return Collections.EMPTY_LIST;
		if(records instanceof Record)
			return Collections.singletonList((Record)records);
		if(records instanceof Collection){
			Collection c = (Collection)records;
			if(c.isEmpty())
				return Collections.EMPTY_LIST;
			Object f = c.iterator().next();
			if(!(f instanceof Record)){
				warn("mvel returned wrong type in list "+f.getClass().getName() + " should be list of records or record");
				return Collections.EMPTY_LIST;
			}
			return c;
		}
		
		warn("mvel returned wrong type "+records.getClass().getName() + " should be list of records or record");
		return Collections.EMPTY_LIST;
	}

}
