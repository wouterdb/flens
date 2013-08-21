package flens.filter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.mvel2.MVEL;
import org.python.core.PyBoolean;
import org.python.core.PyObject;
import org.python.core.PySequenceList;
import org.python.util.PythonInterpreter;

import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

public class JythonFilter extends AbstractFilter {

	private String script;

	public JythonFilter(String name, Tagger tagger, Matcher matcher, int prio,
			String script) {
		super(name, tagger, matcher, prio);
		this.script = script;
	}

	@Override
	public Collection<Record> process(Record in) {
		PythonInterpreter interp = new PythonInterpreter();

		interp.set("values", in.getValues());
		interp.set("DISCARD", false);

		interp.exec(script);

		tag(in);

		PyObject d = interp.get("DISCARD");
		if (d instanceof PyBoolean && ((PyBoolean) d).getBooleanValue())
			in.setType(null);

		PyObject rec = interp.get("records");

		if (rec != null) {
			warn("outputting new records not yet supported for python");
			return Collections.EMPTY_LIST;
		}

		return Collections.EMPTY_LIST;
	}

}
