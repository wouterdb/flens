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

import flens.core.Constants;
import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractListenerInput;

public class OpenTsdbInput extends AbstractListenerInput<BufferedReader> {

	private int port = 4242;
	
	
	public OpenTsdbInput(String name, Tagger tagger, int port) {
		super(name,tagger);
		this.port = port;
	}

	@Override
	protected ServerSocket makeListener() throws IOException {
		return new ServerSocket(port);
	}

	@Override
	public BufferedReader getStream(Socket newSocket) throws IOException {
		return new BufferedReader(new InputStreamReader(
				newSocket.getInputStream()));
	}

	@Override
	public void readAndProcess(BufferedReader in) throws IOException {
		String line = in.readLine();
		if(line==null)
			throw new IOException("connection lost");
		
		Scanner st = new Scanner(line);

		try {
			if (!st.next().equals("put")) {
				warn("bad line", line);
				return;
			}

			String metricName = st.next();
			long time = st.nextLong();
			String metric = st.next();
			
			Map<String, Object> tags = new HashMap<String, Object>();
			
			while(st.hasNext()){
				String tag = st.next();
				String[] parts = tag.split("=");
				tags.put(parts[0], parts.length>1?parts[1]:"");
			}

			String host = (String) tags.remove("host");
			
			if(host == null){
				host = "UNKNOW";
				Logger.getLogger(getClass().getName()).log(Level.WARNING,
						"tsdb metric has no host tag: " + line);
			}
			tags.put(Constants.METRIC, metricName);
			tags.put(Constants.VALUE, metric);
			
			Record r = Record.createWithTimeHostAndValues(time*1000,host,tags);
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
	public void tearDown(BufferedReader in2) throws IOException {
		in2.close();

	}

}
