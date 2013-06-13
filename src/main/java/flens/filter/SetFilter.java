package flens.filter;

import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.tuple.Pair;

public class SetFilter extends AbstractFilter {
	private Map<String, String> pairs = new HashMap<>();

	public SetFilter(String name, Tagger tagger, Matcher matcher,
			List<String> f, List<String> v) {
		super(name, tagger, matcher);
		for (int i = 0; i < f.size(); i++)
			this.pairs.put(f.get(i),v.get(i));
	}

	public Collection<Record> process(Record in) {
		in.getValues().putAll(pairs);
		tag(in);
		return Collections.EMPTY_LIST;
	}
}
