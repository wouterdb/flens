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
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.sun.tools.javac.util.Pair;

import flens.core.Constants;
import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractProcessPoller;
import flens.input.util.StreamPump;

public class NagiosInput extends AbstractProcessPoller {

	private StreamPump err;
	private Tagger errT;
	private NagiosCapture out;
	private String metric;
	private String target;

	/*
	 * Plugin Return Code Service State Host State 0 OK UP 1 WARNING UP or
	 * DOWN/UNREACHABLE* 2 CRITICAL DOWN/UNREACHABLE 3 UNKNOWN DOWN/UNREACHABLE
	 */

	/*
	 * public String[] serviceStates = new
	 * String[]{"OK","WARNING","CRITICAL","UNKNOWN"}; public String[] hostStates
	 * = new String[]{"UP","UP-DOWN","DOWN","DOWN"};
	 */

	public NagiosInput(String name,String plugin, Tagger tagger,Tagger errt, String nagiosdir, String metric, String target,List<String> args, long period) {
		super(name,plugin, tagger, nagiosdir+"/check_"+metric, args, period);
		this.errT = errt;
		this.metric = metric;
		this.target=target;
	}

	public class NagiosCapture extends StreamPump {

		private boolean first = true;
		private String msg;
		private String values;

		public NagiosCapture(String name, BufferedReader s) {
			super(name,null, null, s);
		}

		@Override
		protected void dispatch(String line) {
			if (first) {
				String[] parts = line.split("\\|", 2);
				msg = parts[0];
				if (parts.length == 2)
					values = parts[1];
				first = false;
			}

		}

		public Pair<String, String> getValues() {
			return Pair.of(msg, values);
		}

	}

	@Override
	protected void captureStreams() {
		out = new NagiosCapture(getName() + ".out", new BufferedReader(
				new InputStreamReader(proc.getInputStream())));
		err = new StreamPump(getName() + ".err", getPlugin(),errT, new BufferedReader(
				new InputStreamReader(proc.getErrorStream())));
		err.setInputQueue(in);
		out.start();
		err.start();
	}

	@Override
	protected void postRun() throws InterruptedException {
		out.join();
		err.join();
		
		try{
		Pair<String, String> vals = out.getValues();

		Record r = Record.createWithMetricAndMsg(metric, vals.fst);

		if(target!=null)
			r.getValues().put(Constants.TARGET, target);
		
		if (vals.snd == null) {
			dispatch(r);
		} else {
			String[] types = vals.snd.trim().split(" ");
			for(String typer:types){
				String parts[] = typer.split("=",2);
				Record typerecord = r.doClone();
				typerecord.getValues().put(Constants.TYPE, parts[0]);
				String[] values = parts[1].split(";");
				String unit = values[0].replaceAll("[0-9.]", "");
				values[0] = values[0].replaceFirst(unit, "");
				expandAndDispatch(typerecord,unit,values );
			}
			
			
		}
		}catch(Exception e){
			err("could not parse nagios string" + out.getValues(), e);
		}

		out = null;
		err = null;

	}

	String[] names = new String[]{"value","warn","crit","min","max"};
	
	
	private void expandAndDispatch(Record rec, String unit, String[] values) {

		for (int i = 0; i < values.length; i++) {
			Record out = (Record) rec.doClone();
			try {
				if(values[i].isEmpty())
					continue;
				out.getValues().put(Constants.VALUE, NumberFormat.getInstance().parse(values[i]));
				out.getValues().put(Constants.TYPE_INSTANCE, names[i]);
				out.getValues().put(Constants.UNIT, unit);
				dispatch(out);
			} catch (ParseException e) {
				err("could not parse number" + values[i], e);
			}
			
		}

	}

}
