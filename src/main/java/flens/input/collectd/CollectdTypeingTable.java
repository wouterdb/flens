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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import flens.typing.MetricForm;
import flens.typing.MetricType;
import static flens.util.ParseUtil.*;

public class CollectdTypeingTable extends TypeingTable {

	public CollectdTypeingTable() {
		loadExtendedDB();
	}

	private void loadExtendedDB() {
		try {
			InputStream is = getClass().getResourceAsStream("/collectd/typing.db");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String s = reader.readLine();
			while (s != null) {
				if (!s.trim().startsWith("#"))
					parseLineForExt(s);
				s = reader.readLine();
			}
		} catch (Exception e) {
			warn("could not read extended types for collectd", e);
		}
	}

	private void parseLineForExt(String line) {
		String[] parts = line.split("\\s+");
		// plugin type typeinstance name, unit type low high float
		if (parts.length < 9) {
			warn("bad line in typeing.db, to few parts" + parts.length, line);
			return;
		}
		try {
			if (parts.length == 9)
				add(parts[0], may(parts[1]), may(parts[2]), list(parts[3]), parts[4], parts[5], form(parts[6]), nrLow(parts[7]),
						nrHigh(parts[8]), false);
			else
				add(parts[0], may(parts[1]), may(parts[2]), list(parts[3]), parts[4], parts[5], form(parts[6]), nrLow(parts[7]),
						nrHigh(parts[8]), bool(parts[9]));
		} catch (IllegalArgumentException e) {
			warn("bad line in typeing.db", e);
		}
	}

	
	private void warn(String msg, String s) {
		Logger.getLogger(getClass().getName()).warning(msg + " : " + s);

	}
	
	private void warn(String msg) {
		Logger.getLogger(getClass().getName()).warning(msg);

	}


	private void warn(String msg, Exception e) {

		Logger.getLogger(getClass().getName()).log(Level.WARNING, msg, e);

	}

	

}
