package flens.output;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.output.util.StreamPump;

public class OpenTsdbOutput extends StreamPump {

	private int port = 4242;
	private String host;
	private int reconnectDelay = 10000;
	private int flushOnSize = -1;

	public OpenTsdbOutput(String name, Matcher matcher,String server, int port) {
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
			while(running){
				r = queue.take();
				br.write(String.format("put %s %d %s host=%s\n",r.getValues().get("metric"),r.getTimestamp()/1000,r.getValues().get("value"),r.getSource()));
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

	protected void err(String msg, Exception e) {
		Logger.getLogger(getClass().getName()).log(Level.SEVERE, msg,e);
	}

	protected void warn(String msg, Exception e) {
		Logger.getLogger(getClass().getName()).log(Level.WARNING, msg,e);
		
	}
}
