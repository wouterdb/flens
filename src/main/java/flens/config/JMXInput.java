package flens.config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import flens.core.util.AbstractConfig;
import flens.input.OpenTsdbInput;

public class JMXInput extends AbstractConfig {

	@Override
	protected void construct() {

		List<String> domain = getArray("domains",Collections.EMPTY_LIST);
		int interval = getInt("interval",10000);
		int multiplier = getInt("vm-intervals",10);
		String jvmSelector = get("jvm", ".*");
		
		engine.addInput(new flens.input.JMXInput(name,tagger,jvmSelector,domain,interval,multiplier));
	}

	
	@Override
	protected boolean isIn() {
		return true;
	}

	@Override
	protected boolean isOut() {
		return false;
	}
	
	@Override
	public List<Option> getOptions() {
		List<Option>  out = new LinkedList(super.getOptions());
		out.add(new Option("interval", "int", "10000", "interval between subsequent reports in ms"));
		out.add(new Option("vm-intervals", "int", "10", "search for new VM's every vm-intervals intervals"));
		out.add(new Option("jvm", "String", ".*", "pid of vm or regex on vm name"));
		out.add(new Option("domains", "List", "[]", "jmx domains to report on"));
		return out;
	}


	@Override
	public String getDescription() {
		return "Regualarly searches JMX for metrics, jmx key-value metrics become field-values in flens ";
	}

}
