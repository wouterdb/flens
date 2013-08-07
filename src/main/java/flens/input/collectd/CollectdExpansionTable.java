package flens.input.collectd;



import static flens.core.Constants.*;

public class CollectdExpansionTable extends ExpansionTable {

	public CollectdExpansionTable() {
		init();
	}
	
	protected void init() {
		add("interface", null, new String[] { "in", "out" });
		add("load", null, new String[] { "1m", "5m","15m" });

	}
	

}
