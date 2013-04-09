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
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.Pair;

import flens.core.Constants;
import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractListenerInput;

public class GraphiteInput extends AbstractListenerInput<Pair<String,BufferedReader>> {

	private int port = 2003;
	
	
	public GraphiteInput(String name, Tagger tagger, int port) {
		super(name,tagger);
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
		
		Scanner st = new Scanner(line);

		try {
			
			String metricName = st.next();
			String metric = st.next();
			long time = st.nextLong();
			
			
			Map<String, Object> tags = new HashMap<String, Object>();
			
			tags.put(Constants.METRIC, metricName);
			tags.put(Constants.VALUE, metric);
			
			Record r = new Record(null,time*1000,host,tags);
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
