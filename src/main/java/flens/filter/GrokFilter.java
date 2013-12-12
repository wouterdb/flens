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

import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;

import com.nflabs.Grok.Grok;
import com.nflabs.Grok.GrokError;
import com.nflabs.Grok.Match;

import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

public class GrokFilter extends AbstractFilter {

	private String script;
	private Grok compiled;
	private String field;

	public GrokFilter(String name, Tagger tagger, Matcher matcher,int prio,
			String script, String inField) {
		super(name, tagger, matcher,prio);
		this.script = script;
		this.field = inField;
		start();
	}

	private void start() {
		// Compile the expression.
		compiled = new Grok();
		try {
			compiled.addPatternFromReader(new InputStreamReader(
					GrokFilter.class.getResourceAsStream("/grok/base")));

			compiled.addPatternFromReader(new InputStreamReader(
					GrokFilter.class.getResourceAsStream("/grok/java")));
		} catch (Throwable e) {
			throw new IllegalArgumentException("could not load aux patterns", e);
		}
		int re = compiled.compile(script);
		if (re != GrokError.GROK_OK)
			throw new IllegalArgumentException("bad grok pattern");
	}

	@Override
	public Collection<Record> process(Record in) {
		String inf = (String) in.getValues().get(field);
		if (inf == null)
			return Collections.EMPTY_LIST;
		Match m = compiled.match(inf);
		if (m == null || m.isNull())
			return Collections.EMPTY_LIST;
		m.captures();

		in.getValues().putAll(m.toMap());
		tag(in);
		return Collections.EMPTY_LIST;
	}

}
