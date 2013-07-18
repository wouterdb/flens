package flens.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractActiveInput;
import flens.input.util.AbstractInput;
import flens.input.util.StreamPump;


public class ProcessPoller extends AbstractInput {

	private String cmd;
	private Process proc;
	private StreamPump out;
	private StreamPump err;
	private Tagger outT;
	private Tagger errT;
	private List args;
	private Timer t;
	private long period;

	public ProcessPoller(String name, Tagger out,Tagger err, String cmd,List<String> args, long period) {
		super(name, null);
		this.cmd=cmd;
		this.args = args;
		this.outT = out;
		this.errT = err;
		args.add(0, cmd);
	}

	public synchronized void poll() throws InterruptedException {
		
		
		ProcessBuilder pb = new ProcessBuilder(args);
		try {
			proc = pb.start();
		} catch (IOException e) {
			err("could not start process", e);
			return;
		}
		
		out = new StreamPump(getName()+".out",outT,new BufferedReader(new InputStreamReader(proc.getInputStream()))) ;
		err = new StreamPump(getName()+".err",errT,new BufferedReader(new InputStreamReader(proc.getErrorStream()))) ;
		out.setInputQueue(in);
		err.setInputQueue(in);
		out.start();
		err.start();
		
		proc.waitFor();
		out.join();
		err.join();
		out=null;
		err=null;
		notify();
	}

	public synchronized void stop() {
		t.cancel();
		if(proc!=null)
			proc.destroy();
	}

	public synchronized void join() throws InterruptedException {
		if(out!=null)
			wait();
			
	}

	@Override
	public void start() {
		t = new Timer(getName());
		t.schedule(new TimerTask(){

			@Override
			public void run() {
				try {
					poll();
				} catch (InterruptedException e) {
					err("failed to poll",e);
				}
				
			}}, 0, period);
		
	}

}
