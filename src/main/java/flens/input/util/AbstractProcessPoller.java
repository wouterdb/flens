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
package flens.input.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import flens.core.Record;
import flens.core.Tagger;


public abstract class AbstractProcessPoller extends AbstractInput {

	private String cmd;
	protected Process proc;
	private List args;
	private Timer t;
	private long period;
	private boolean running;

	public AbstractProcessPoller(String name, Tagger t,String cmd,List<String> args, long period) {
		super(name, t);
		this.cmd=cmd;
		this.args = new LinkedList<>(args);
		this.period=period;
		this.args.add(0, cmd);
	}
	
	public synchronized void poll() throws InterruptedException {
		
		
		ProcessBuilder pb = new ProcessBuilder(args);
		try {
			proc = pb.start();
		} catch (IOException e) {
			err("could not start process", e);
			return;
		}
		
		captureStreams();
		
		proc.waitFor();
		
		postRun();
		running=false;
		proc = null;
		notify();
	}

	protected abstract void captureStreams();
	protected abstract void postRun() throws InterruptedException;

	public synchronized void stop() {
		t.cancel();
		if(proc!=null)
			proc.destroy();
	}

	public synchronized void join() throws InterruptedException {
		if(running)
			wait();	
	}

	@Override
	public synchronized void start() {
		running=true;
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
