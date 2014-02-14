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
package flens.input.collectd;

import static flens.core.Constants.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.logging.Logger;

public class CollectdExpansionTable extends ExpansionTable {

	public CollectdExpansionTable() {
		init();
		loadTypesDB();
	}
	
	protected void init() {
		add("interface", null, new String[] { "in", "out" });
		add("load", null, new String[] { "1m", "5m", "15m" });
		add("disk", null, new String[] { "read", "write" });
	}

	private void loadTypesDB() {
		File f = new File("/usr/share/collectd/types.db");
		if (!f.canRead())
			return;
		try {
			BufferedReader r = new BufferedReader(new FileReader(f));

			try {
				String s = r.readLine();
				while (s != null) {
					if(!s.trim().startsWith("#"))
						parseLine(s);					
					s = r.readLine();
				}
			} finally {
				r.close();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void parseLine(String s) {
		String[] parts = s.split("\t",2);
		if(parts.length != 2)
			warn("line not split correctly",s);
		else
			parseForType(parts[0],parts[1]);
	}

	private void warn(String msg, String s) {
		Logger.getLogger(getClass().getName()).warning(msg + " : " + s);
		
	}

	private void parseForType(String type, String righthand) {
		String[] parts = righthand.split(",");
		//skip single values, not expanded anyway
		if(parts.length==1)
			return;
		String[] names = new String[parts.length];
		for (int i = 0; i < names.length; i++) {
			names[i]=parts[i].trim().split(":")[0];
		}
		add(null,type,names);
		//System.out.println(type + 	Arrays.deepToString(names));
	}


}
