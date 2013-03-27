package flens.core.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractPlugin {
	
	public void warn(String line) {
		Logger.getLogger(getClass().getName()).log(Level.WARNING,
				getName() + ": " + line  + "");
		
	}
	
	protected void err(String msg, Exception e) {
		Logger.getLogger(getClass().getName()).log(Level.SEVERE, msg,e);
	}

	protected void warn(String msg, Exception e) {
		Logger.getLogger(getClass().getName()).log(Level.WARNING, msg,e);
		
	}

	public abstract String getName() ;

}
