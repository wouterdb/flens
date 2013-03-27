package flens.core.util;

import java.util.List;

import flens.core.Record;
import flens.core.Tagger;

public class InputTagger implements Tagger {

	private String type;
	private List<String> tags;

	public InputTagger(String type, List<String> tags) {
		if(type == null)
			throw new IllegalArgumentException("no type given");
		this.type = type;
		this.tags = tags;
	}

	@Override
	public void adapt(Record r) {
		r.getTags().addAll(tags);
		r.setType(type);
	}

}
