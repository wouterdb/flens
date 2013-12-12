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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
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
import org.elasticsearch.common.mvel2.ParserContext;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.output.util.AbstractPumpOutput;
import flens.util.MVELUtil;

public class SocketOutput extends AbstractPumpOutput {

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

	private int port;
	private String host;
	private Thread errPump;
	private String field;

	public SocketOutput(String name, Matcher matcher, String server, int port,
			String field) {
		super(name, matcher);
		this.port = port;
		this.host = server;
		this.field = field;
	}

	@Override
	public void run() {
		Socket s = null;
		Record r = null;
		try {
			s = new Socket(host, port);
			s.setTcpNoDelay(true);
			DataOutputStream br = new DataOutputStream(s.getOutputStream());
			hookOnErrpump(s);
			while (running) {
				r = queue.take();

				Object value = r.getValues().get(field);
				byte[] bx = getBytes(value);
				br.writeInt(bx.length);
				br.write(bx);
				br.flush();
				sent++;
			}
		} catch (UnknownHostException e) {

			err(getName() + " host not know", e);
		} catch (IOException e) {
			lost++;
			err(getName() + " pipe broken, going into reconnect", e);
			reconnect();
		} catch (InterruptedException e) {
			// normal
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

	@Override
	public void stop() {
		super.stop();
		if (errPump != null)
			errPump.interrupt();
	}

}
