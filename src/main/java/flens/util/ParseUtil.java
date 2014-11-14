package flens.util;

import flens.typing.MetricForm;

public class ParseUtil {

	public static String may(String in) {
		if (in.equals("*"))
			return null;
		return in;
	}

	public static String[] list(String in) {
		return in.split(",");
	}

	public static Number nrHigh(String in) {
		if(in.equals("-"))
			return Double.POSITIVE_INFINITY;
		return Long.parseLong(in);

	}

	public static Number  nrLow(String in) {
		if(in.equals("-"))
			return Double.NEGATIVE_INFINITY;
		return Long.parseLong(in);

	}

	
	public static Boolean bool(String in) {
		return Boolean.parseBoolean(in);
	}
	
	public static MetricForm form(String in){
		return MetricForm.parse(in);
	}
}
