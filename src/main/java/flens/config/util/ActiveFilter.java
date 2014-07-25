package flens.config.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import flens.core.Config;
import flens.core.Flengine;

public abstract class ActiveFilter extends AbstractConfig {

	protected boolean isIn() {
		return true;
	}

	protected boolean isOut() {
		return false;
	}

	protected boolean isQuery() {
		return false;
	}

	protected boolean isFilter() {
		return false;
	}

	@Override
	public void readConfigPart(String name, Map<String, Object> tree, Flengine engine) {
		this.tree = tree;
		this.engine = engine;
		this.name = name;
		this.plugin = get("plugin", name);

		logger.info("starting: " + name);

		matcher = readMatcher();
		tagger = readTagger("out-");

		construct();

		if (!tree.isEmpty())
			warn("unknown values {0}", tree);
	}

	@Override
	public List<Option> getOptions() {
	
		List<Option> matcherOpts = new LinkedList<Config.Option>();
		matcherOpts.add(new Option("name", "String", "plugin name",
				"name of the filter, for reporting and monitoring purposes"));
		
		matcherOpts.add(new Option("type", "String", "name", "only apply to records having this type"));
		matcherOpts.add(new Option("tags", "[String]", "[]", "only apply to records having all of these tags"));
		
		matcherOpts.add(new Option("out-type", "String", "name" ,"type to apply to the records"));
		matcherOpts.add(new Option("out-add-tags", "[String]", "[]", "add following tags"));
		matcherOpts.add(new Option("out-remove-tags", "[String]", "[]", "remove following tags"));
		return matcherOpts;
	}

}
