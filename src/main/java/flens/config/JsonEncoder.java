package flens.config;

import java.util.List;

import flens.core.util.AbstractConfig;

public class JsonEncoder extends AbstractConfig{

	@Override
	protected boolean isIn() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void construct() {
		String field = get("field", "json");
		List<String> fields = getArray("fields", null);
		
		engine.addFilter(new flens.filter.JsonEncoder(name,tagger,matcher,prio,field,fields));
	}

	@Override
	protected boolean isOut() {
		return false;
	}

	@Override
	public String getDescription() {
		return "encode json messages";
	}

}
