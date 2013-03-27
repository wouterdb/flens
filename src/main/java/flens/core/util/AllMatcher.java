package flens.core.util;

import flens.core.Matcher;
import flens.core.Record;

public class AllMatcher implements Matcher {

	public boolean matches(Record r) {
		return true;
	}

}
