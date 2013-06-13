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

	public GrokFilter(String name, Tagger tagger, Matcher matcher,
			String script, String inField) {
		super(name, tagger, matcher);
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
