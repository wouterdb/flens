package flens.core.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractPlugin {
	
	public void warn(String line) {
		Logger.getLogger(getClass().getName()).log(Level.WARNING,
				getName() + ": " + line  + "");
		
	}
	
	protected void err(String msg, Throwable e) {
		Logger.getLogger(getClass().getName()).log(Level.SEVERE, msg,e);
	}

	protected void warn(String msg, Exception e) {
		Logger.getLogger(getClass().getName()).log(Level.WARNING, msg,e);
		
	}
	
	protected void info(String msg) {
		Logger.getLogger(getClass().getName()).log(Level.INFO, msg);
		
	}

	public abstract String getName();

}
