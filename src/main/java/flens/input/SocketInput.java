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
