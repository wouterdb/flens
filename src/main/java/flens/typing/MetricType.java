package flens.typing;

public class MetricType {
	
	private String name;
	private String unit;
	
	private String resource;
	
	private MetricForm form;
	
	private Number minValue;
	private Number maxValue;
	
	private boolean integer;

	
	
	
	public MetricType(String name, String unit, String resource, MetricForm form, Number minValue, Number maxValue, boolean integer) {
		super();
		this.name = name;
		this.unit = unit;
		this.form = form;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.integer = integer;
		this.resource = resource;
	}

	/**
	 * name of the record type 
	 * 
	 * convention: 
	 *   for raw metric: agent.resource.sub  (collectd.cpu.idle)
	 *   for normalized metrics: scope.resource.sub (sys.cpu.idle)
	 */
	public String getName() {
		return name;
	}

	public String getUnit() {
		return unit;
	}

	public MetricForm getForm() {
		return form;
	}

	public Number getMinValue() {
		return minValue;
	}

	public Number getMaxValue() {
		return maxValue;
	}

	public boolean isInteger() {
		return integer;
	}
	
	public String getResource() {
		return resource;
	}

	public String getRange() {
		String start = Double.isInfinite(minValue.doubleValue())?"],":"["+minValue+",";
		String end = Double.isInfinite(maxValue.doubleValue())?"[":maxValue+"]";
		return start+end;
	}

}
