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
import flens.input.util.AbstractProcessPoller;
import flens.input.util.StreamPump;


public class ProcessPoller extends AbstractProcessPoller {

	private StreamPump out;
	private StreamPump err;
	private Tagger outT;
	private Tagger errT;


	public ProcessPoller(String name, Tagger out,Tagger err, String cmd,List<String> args, long period) {
		super(name,null, cmd, args, period);
		this.outT = out;
		this.errT = err;
	}


	@Override
	protected void captureStreams() {
		out = new StreamPump(getName()+".out",outT,new BufferedReader(new InputStreamReader(proc.getInputStream()))) ;
		err = new StreamPump(getName()+".err",errT,new BufferedReader(new InputStreamReader(proc.getErrorStream()))) ;
		out.setInputQueue(in);
		err.setInputQueue(in);
		out.start();
		err.start();
		
	}

	@Override
	protected void postRun() throws InterruptedException {
		out.join();
		err.join();
		out=null;
		err=null;
		
	}

}
