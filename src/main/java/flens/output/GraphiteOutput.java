package flens.output;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.Pair;

import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.output.util.AbstractPumpOutput;

public class GraphiteOutput extends AbstractPumpOutput {

	public class ErrPump implements Runnable {

		private BufferedReader reader;

		public ErrPump(InputStream in) {
			reader = new BufferedReader(new InputStreamReader(in));
		}

		@Override
		public void run() {
			try {
				while(running){
					warn("got: " + reader.readLine());
				}
					
			}catch (Exception e) {
				err("error punp broke",e);
			}

		}

	}

	private int port = 2003;
	private String host;
	private int reconnectDelay = 10000;
	private int flushOnSize = -1;
	private Thread errPump;
	
	public GraphiteOutput(String name, Matcher matcher,String server, int port) {
		super(name,matcher);
		this.port = port;
		this.host = server;
	}

	
	@Override
	public void run() {
		Socket s = null;
		Record r = null;
		try {
			s = new Socket(host, port);
			s.setTcpNoDelay(true);
			BufferedWriter br = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			hookOnErrpump(s);
			while(running){
				r = queue.take();
				br.write(String.format("%s %s %d\n",r.getValues().get("metric"),r.getValues().get("value"),r.getTimestamp()/1000));
				//System.out.println(String.format("put %s %d %s host=%s",r.getValues().get("metric"),r.getTimestamp(),r.getValues().get("value"),r.getSource()));
				br.flush();
			}
		} catch (UnknownHostException e) {
			
			err(getName()+ " host not know",e);
		} catch (IOException e) {
			err(getName()+ " pipe broken, going into reconnect",e);
			reconnect();
		} catch (InterruptedException e) {
			//normal
		}finally{
			if(s!=null)
				try {
					s.close();
				} catch (IOException e) {
					warn(getName()+ "could not close socket",e);
				}
		}
		
		
	}
	

	private void hookOnErrpump(Socket s) {
		try {
			errPump = new Thread(new ErrPump(s.getInputStream()));
		} catch (IOException e) {
			err(" err pipe setup failed",e);
		}
		
	}

	protected void reconnect(){
		//FIXME:may lose records
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			
			@Override
			public void run() {
				if(flushOnSize>0 && getOutputQueue().size()>flushOnSize)
					getOutputQueue().clear();
				start();
			}
		}, reconnectDelay );
		
		
	}

	@Override
	public void stop() {
		super.stop();
		if(errPump!=null)
			errPump.interrupt();
	}
	
}