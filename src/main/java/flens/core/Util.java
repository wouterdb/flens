package flens.core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class Util {

	private static String hostname;
	
	public static void overriderHostname(String name){
		hostname=name;
	}

	public static String hostName() {
		if(hostname == null){
			 try {
				hostname = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				hostname = "localhost";
				Logger.getLogger("flens.core").warning("could not find hostname");
			}
		}
		return hostname ;
	}

}
