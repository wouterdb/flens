package flens.filter;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import flens.core.Constants;
import flens.core.Filter;
import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

public class UnBatchingFilter extends AbstractFilter implements Filter{

	public UnBatchingFilter(String name, String plugin, Tagger tagger, Matcher matcher,int prio) {
		super( name, plugin, tagger, matcher,prio);	
	}

	@Override
	public Collection<Record> process(Record in) {
		List<Record> outs = new LinkedList<Record>();
		
		if(in.getValues().containsKey(Constants.SUBRECORDS)){
			List<Record> list = (List<Record>) in.getValues().get(Constants.SUBRECORDS);
			for (Record record : list) {
				outs.add(tag(record));
			}
			in.setType(null);
		}
		
		return outs;
	}

}
