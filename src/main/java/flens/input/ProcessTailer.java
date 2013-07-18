package flens.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractActiveInput;
import flens.input.util.AbstractInput;
import flens.input.util.StreamPump;


public class ProcessTailer extends AbstractInput {

	private String cmd;
	private Process proc;
	private StreamPump out;
	private StreamPump err;
	private Tagger outT;
	private Tagger errT;
	private List args;

	public ProcessTailer(String name, Tagger out,Tagger err, String cmd,List<String> args) {
		super(name, null);
		this.cmd=cmd;
		this.args = args;
		this.outT = out;
		this.errT = err;
	}

	@Override
	public void start() {
		args.add(0, cmd);
		ProcessBuilder pb = new ProcessBuilder(args);
		try {
			proc = pb.start();
		} catch (IOException e) {
			err("could not start process", e);
			return;
		}
		
	/*	new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					proc.waitFor();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("failed");
				stop();
			}
		}).start();*/
		
		out = new StreamPump(getName()+".out",outT,new BufferedReader(new InputStreamReader(proc.getInputStream()))) ;
		err = new StreamPump(getName()+".err",errT,new BufferedReader(new InputStreamReader(proc.getErrorStream()))) ;
		out.setInputQueue(in);
		err.setInputQueue(in);
		out.start();
		err.start();
	}

	public void stop() {
		proc.destroy();
		out.stop();
		err.stop();
	}

	public void join() throws InterruptedException {
		proc.waitFor();
		err.join();
		out.join();
	}

}
