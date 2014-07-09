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
package flens.output;

import java.io.PrintStream;

import flens.core.Matcher;
import flens.core.Output;
import flens.core.Record;
import flens.output.util.AbstractPumpOutput;

public class SystemOut extends AbstractPumpOutput implements Output {

	private PrintStream stream;
	
	public SystemOut(String name,String plugin, Matcher matcher) {
		super(name,plugin, matcher);
		stream = System.out;
	}

	public void run() {
		
		try {
			while (running) {
				Record r = queue.take();
				stream.println(String.format("[%s] %s",getName(),r.toLine()));
				sent++;
			}
		} catch (InterruptedException e) {
			// break loop
		}

	}
}
