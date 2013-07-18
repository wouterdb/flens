package flens.filter;

import java.util.Collection;
import java.util.Collections;

import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

public class CharacterDecoder extends AbstractFilter {

	public CharacterDecoder(String name, Tagger tagger, Matcher matcher,int prio) {
		super(name, tagger, matcher,prio);
	}

	@Override
	public Collection<Record> process(Record in) {
		//TODO config
		String encoding = (String) in.getValues().get("encoding");
		//TODO encoding
		String s = new String((byte[])in.getValues().get("body"));
		in.getValues().put("message", s);
		tag(in);
		return Collections.EMPTY_LIST;
	}

}
