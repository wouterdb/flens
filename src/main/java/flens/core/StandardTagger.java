package flens.core;

import java.util.List;

public class StandardTagger implements Tagger {
	private List<String> tags;

	public StandardTagger(List<String> tags) {
		
		this.tags = tags;
	}

	@Override
	public void adapt(Record r) {
		r.getTags().addAll(tags);
	}

}
