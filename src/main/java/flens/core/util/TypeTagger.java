package flens.core.util;

import flens.core.Record;
import flens.core.Tagger;

public class TypeTagger implements Tagger {

	private String type;

	public TypeTagger(String stype) {
		this.type=stype;
	}

	@Override
	public void adapt(Record r) {
		r.setType(type);
	}

}
