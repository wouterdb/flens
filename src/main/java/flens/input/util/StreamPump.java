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
package flens.input.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.concurrent.BlockingQueue;

import flens.core.Input;
import flens.core.Record;
import flens.core.Tagger;
import flens.core.util.AbstractPlugin;

public class StreamPump extends AbstractActiveInput implements Runnable {

	private BufferedReader reader;

	public StreamPump(String name, String plugin, Tagger tagger, BufferedReader s) {
		super(name,plugin, tagger);
		this.reader = s;
	}

	@Override
	public void run() {
		try {
			while (running) {
				String r = reader.readLine();
				if (r == null)
					running = false;
				else
					dispatch(r);
			}
		} catch (IOException e) {
			err("stream failed", e);
		}
		running = false;
	}

	protected void dispatch(String s) {
		dispatch(new Record(s));
	}

	protected void dispatch(Record r) {
		if (tagger != null)
			super.dispatch(r);
	}
}
