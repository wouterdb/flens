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
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

import flens.core.Constants;
import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractListenerInput;

public class SocketInput extends
		AbstractListenerInput<Pair<String, DataInputStream>> {

	private int port;

	public SocketInput(String name, Tagger tagger, int port) {
		super(name, tagger);
		this.port = port;
	}

	@Override
	protected ServerSocket makeListener() throws IOException {
		return new ServerSocket(port);
	}

	@Override
	public Pair<String, DataInputStream> getStream(Socket newSocket)
			throws IOException {
		String hostname = newSocket.getInetAddress().getHostName();
		return Pair.of(hostname,
				new DataInputStream(newSocket.getInputStream()));
	}

	@Override
	public void readAndProcess(Pair<String, DataInputStream> inx)
			throws IOException {
		DataInputStream in = inx.getRight();
		String host = inx.getLeft();
		int line = in.readInt();

		byte[] block = new byte[line];

		IOUtils.readFully(in, block);

		Map<String, Object> values = new HashMap<String, Object>();

		values.put(Constants.BODY, block);
		Record r = Record.createWithTimeHostAndValues(
				System.currentTimeMillis(), host, values);
		dispatch(r);

	}

	@Override
	public void tearDown(Pair<String, DataInputStream> in2) throws IOException {
		in2.getRight().close();
	}

}