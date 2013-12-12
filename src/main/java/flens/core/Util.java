/**
 *
 *     Copyright 2013 KU Leuven Research and Development - iMinds - Distrinet
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     Administrative Contact: dnet-project-office@cs.kuleuven.be
 *     Technical Contact: wouter.deborger@cs.kuleuven.be
 */
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
