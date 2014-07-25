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
package flens.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.Pair;

import flens.core.Constants;
import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractListenerInput;

public class GraphiteInput extends AbstractListenerInput<Pair<String,BufferedReader>> {

	private int port = 2003;
	
	
	public GraphiteInput(String name,String plugin, Tagger tagger, int port) {
		super(name,plugin,tagger);
		this.port = port;
	}

	@Override
	protected ServerSocket makeListener() throws IOException {
		return new ServerSocket(port);
	}

	@Override
	public Pair<String,BufferedReader> getStream(Socket newSocket) throws IOException {
		String hostname = newSocket.getInetAddress().getHostName();
		return Pair.of(hostname,new BufferedReader(new InputStreamReader(
				newSocket.getInputStream())));
	}

	@Override
	//metric_path value timestamp\n  
	//http://graphite.wikidot.com/getting-your-data-into-graphite
	public void readAndProcess(Pair<String,BufferedReader> inx) throws IOException {
		BufferedReader in = inx.getRight();
		String host = inx.getLeft();
		String line = in.readLine();
		if(line==null)
			throw new IOException("connection lost");
		
		try(Scanner st = new Scanner(line);){			
			String metricName = st.next();
			String metric = st.next();
			long time = st.nextLong();
			
			
			Map<String, Object> tags = new HashMap<String, Object>();
			
			tags.put(Constants.METRIC, metricName);
			tags.put(Constants.VALUE, metric);
			
			Record r =  Record.createWithTimeHostAndValues(time*1000,host,tags);
			dispatch(r);
		} catch (NoSuchElementException e) {
			warn("line too short", line);
		}

	}

	private void warn(String msg, String line) {
		Logger.getLogger(getClass().getName()).log(Level.WARNING,
				"opentsdb: " + msg + " [ " + line  + "]");
		
	}

	@Override
	public void tearDown(Pair<String,BufferedReader> in2) throws IOException {
		in2.getRight().close();
	}

}
