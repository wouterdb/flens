package flens.filter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import flens.core.Constants;
import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

public class TagFilter extends AbstractFilter {

	private Map<String,String> tags;

	public TagFilter(String name, Tagger tagger, Matcher matcher,
			Map<String,String> tags) {
		super(name, tagger, matcher);
		this.tags = tags;
	}

	@Override
	public Collection<Record> process(Record in) {
		in.setValue(Constants.TAGS, tags);
		return Collections.EMPTY_LIST;
	}

}
