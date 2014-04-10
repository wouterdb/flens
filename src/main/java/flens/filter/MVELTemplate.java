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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mvel2.CompileException;
import org.mvel2.MVEL;
import org.mvel2.UnresolveablePropertyException;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;
import flens.util.MVELUtil;

public class MVELTemplate extends AbstractFilter {

	private static final Logger log = Logger.getLogger(MVELTemplate.class
			.getName());

	private String[] collumnNames;
	private CompiledTemplate[] collumnTemplates;

	public MVELTemplate(String name, Tagger tagger, Matcher matcher, int prio,
			List<String> field, List<String> collumnTemplates) {
		super(name, tagger, matcher, prio);
		this.collumnNames = field.toArray(new String[0]);
		this.collumnTemplates = new CompiledTemplate[collumnTemplates.size()];
		for (int i = 0; i < collumnTemplates.size(); i++) {
			this.collumnTemplates[i] = MVELUtil
					.compileTemplateTooled(collumnTemplates.get(i));
		}
	}

	@Override
	public Collection<Record> process(Record in) {

		try {
			Object[] out = new Object[this.collumnTemplates.length];
			for (int i = 0; i < this.collumnTemplates.length; i++) {
				out[i] = TemplateRuntime.execute(this.collumnTemplates[i],
						in.getValues());
			}

			for (int i = 0; i < out.length; i++) {
				in.getValues().put(collumnNames[i], out[i]);
			}

			tag(in);
		} catch (CompileException | UnresolveablePropertyException e) {
			log.log(Level.SEVERE, "MVEL failed, context: " + in.getValues(), e);
		}
		return Collections.EMPTY_LIST;

		/*
		 * if(records == null) return Collections.EMPTY_LIST; if(records
		 * instanceof Record) return Collections.singletonList((Record)records);
		 * if(records instanceof Collection){ Collection c =
		 * (Collection)records; if(c.isEmpty()) return Collections.EMPTY_LIST;
		 * Object f = c.iterator().next(); if(!(f instanceof Record)){
		 * warn("mvel returned wrong type in list "+f.getClass().getName() +
		 * " should be list of records or record"); return
		 * Collections.EMPTY_LIST; } return c; }
		 * 
		 * warn("mvel returned wrong type "+records.getClass().getName() +
		 * " should be list of records or record"); return
		 * Collections.EMPTY_LIST;
		 */
	}

}
