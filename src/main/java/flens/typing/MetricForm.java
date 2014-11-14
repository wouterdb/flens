package flens.typing;

public enum MetricForm {
	
	Gauge,Counter,Absolute,Other;

	
	public static MetricForm parse(String in) {
		in=in.substring(0, 1).toLowerCase();
		if(in.startsWith("g")){
			return Gauge;
		}else if(in.startsWith("c")){
			return Counter;
		}else if(in.startsWith("a")){
			return Absolute;
		}else if(in.startsWith("o")){
			return Other;
		}
		
		throw new IllegalArgumentException("form unkown: " + in);
	}

}
