package flens.core;

import java.util.List;
import java.util.Map;

public interface Config {

	public static class Option {
		private String name;
		private String type;
		private String descr;
		private String defaultv;

		public Option(String name, String type, String defaultv, String descr) {
			super();
			this.name = name;
			this.type = type;
			this.descr = descr;
			this.defaultv = defaultv;
		}

		public String getName() {
			return name;
		}

		public String getType() {
			return type;
		}

		public String getDescr() {
			return descr;
		}

		public String getDefaultv() {
			return defaultv;
		}

		
	}

	public void readConfigPart(String name, Map<String, Object> tree,
			Flengine engine);

	public List<Option> getOptions();

	public String getDescription();

}
