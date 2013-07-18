package flens.filter;

import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.tuple.Pair;

public class RenameFilter extends AbstractFilter {
	private List<Pair<String, String>> names = new LinkedList<>();

	public RenameFilter(String name, Tagger tagger, Matcher matcher,int prio,
			List<String> f, List<String> t) {
		super(name, tagger, matcher,prio);
		for (int i = 0; i < f.size(); i++)
			this.names.add(Pair.of(f.get(i), t.get(i)));
	}

	public Collection<Record> process(Record in) {
		Map vals = in.getValues();
		for (Pair ren : this.names) {
			Object val = vals.remove(ren.getKey());
			if (val != null) {
				if(!((String)ren.getRight()).isEmpty())
					vals.put(ren.getRight(), val);
			}
		}
		
		tag(in);
		return Collections.EMPTY_LIST;
	}
}
