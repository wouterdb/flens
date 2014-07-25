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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import flens.core.Tagger;
import flens.input.util.AbstractInput;
import flens.input.util.StreamPump;


public class ProcessTailer extends AbstractInput {

	private String cmd;
	private Process proc;
	private StreamPump out;
	private StreamPump err;
	private Tagger outT;
	private Tagger errT;
	private List<String> args;

	public ProcessTailer(String name,String plugin, Tagger out,Tagger err, String cmd,List<String> args) {
		super(name,plugin, null);
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
		
		out = new StreamPump(getName()+".out",getPlugin(),outT,new BufferedReader(new InputStreamReader(proc.getInputStream()))) ;
		err = new StreamPump(getName()+".err",getPlugin(),errT,new BufferedReader(new InputStreamReader(proc.getErrorStream()))) ;
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
