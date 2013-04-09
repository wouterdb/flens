package flens.input.jmx;

import java.util.regex.PatternSyntaxException;

public class VMSelectionException extends Exception {

	public VMSelectionException(String msg) {
		super(msg);
	}

	public VMSelectionException(String msg, Exception e) {
		super(msg, e);
	}

}
