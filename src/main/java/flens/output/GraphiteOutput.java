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
package flens.output;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import org.mvel2.UnresolveablePropertyException;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;

import flens.core.Matcher;
import flens.core.Record;
import flens.output.util.AbstractPumpOutput;
import flens.util.MVELUtil;

public class GraphiteOutput extends AbstractPumpOutput {

	public class ErrPump implements Runnable {

		private BufferedReader reader;

		public ErrPump(InputStream in) {
			reader = new BufferedReader(new InputStreamReader(in));
		}

		@Override
		public void run() {
			try {
				while (running) {
					warn("got: " + reader.readLine());
				}

			} catch (Exception e) {
				err("error punp broke", e);
			}

		}

	}

	private int port = 2003;
	private String host;
	private int reconnectDelay = 10000;
	private int flushOnSize = -1;
	private Thread errPump;

	private CompiledTemplate index;
	private String metric;

	public GraphiteOutput(String name,String plugin, Matcher matcher, String server, int port, String metric) {
		super(name,plugin, matcher);
	
		this.port = port;
		this.host = server;

		this.metric=metric;

		this.index = MVELUtil.compileTemplateTooled(metric);
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
			while (running) {
				r = queue.take();

				try {

					String metric = (String) TemplateRuntime.execute(this.index, r.getValues());
					Object value = r.getValues().get("value");
					if (value != null)
						br.write(String.format("%s %s %d\n", metric, value, r.getTimestamp() / 1000));
					// System.out.println(String.format("put %s %d %s host=%s",r.getValues().get("metric"),r.getTimestamp(),r.getValues().get("value"),r.getSource()));
					br.flush();
					sent++;

				} catch (UnresolveablePropertyException e) {
					lost++;
					err(getName() + " bad mvel template, dropping record: " + r.toLine(), e);
				}

			}

		} catch (UnknownHostException e) {
			lost++;
			err(getName() + " host not known, going into reconnect", e);
			reconnect();
		} catch (IOException e) {
			lost++;
			err(getName() + " pipe broken, going into reconnect", e);
			reconnect();
		} catch (InterruptedException e) {
			// normal
		} catch (Exception e) {
			lost++;
			err(getName() + "unexcpected exception, going into reconnect", e);
			reconnect();
		} finally {
			if (s != null)
				try {
					s.close();
				} catch (IOException e) {
					warn(getName() + "could not close socket", e);
				}
		}

	}

	private void hookOnErrpump(Socket s) {
		try {
			errPump = new Thread(new ErrPump(s.getInputStream()));
		} catch (IOException e) {
			err(" err pipe setup failed", e);
		}

	}

	protected void reconnect() {
		// FIXME:may lose records
		Timer t = new Timer();
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				if (flushOnSize > 0 && getOutputQueue().size() > flushOnSize)
					getOutputQueue().clear();
				start();
			}
		}, reconnectDelay);

	}

	@Override
	public void stop() {
		super.stop();
		if (errPump != null)
			errPump.interrupt();
	}

}
