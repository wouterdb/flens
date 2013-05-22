package flens.core.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import flens.core.Matcher;
import flens.core.Record;

public class StandardMatcher implements Matcher {

	private Set<String> tags;
	private String type;

	public StandardMatcher(String type, List tags) {
		this.type = type;
		this.tags = new HashSet<String>(tags);
	}

	@Override
	public boolean matches(Record r) {
		if(type != null && !r.getType().equals(type))
			return false;
		return r.getTags().containsAll(tags);
			
	}

}
