package flens.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StandardMatcher implements Matcher {

	private Set<String> tags;
	private String type;

	public StandardMatcher(String type, List tags) {
		this.type = type;
		this.tags = new HashSet<String>(tags);
		System.out.println(this.tags);
	}

	@Override
	public boolean matches(Record r) {
		if(type != null && !r.getType().equals(type))
			return false;
		return r.getTags().containsAll(tags);
			
	}

}
