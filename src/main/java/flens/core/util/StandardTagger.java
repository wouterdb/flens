package flens.core.util;

import java.util.List;

import flens.core.Record;
import flens.core.Tagger;

public class StandardTagger implements Tagger {
	private List<String> tags;
	private List rtags;
	private String type;

	public StandardTagger(String type,List<String> tags, List rtags) {
		this.tags = tags;
		this.rtags = rtags;
		this.type=type;
	}



	@Override
	public void adapt(Record r) {
		r.getTags().addAll(tags);
		r.getTags().removeAll(rtags);
		if(type!=null)
			r.setType(type);
	}

}
