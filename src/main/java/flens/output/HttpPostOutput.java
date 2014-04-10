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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;

import flens.core.Matcher;
import flens.core.Record;
import flens.output.util.AbstractPumpOutput;
import flens.output.util.Influxdb;
import flens.util.MVELUtil;

public class HttpPostOutput extends AbstractPumpOutput {

	private URL url;

	private String[] collumnNames;
	private CompiledTemplate[] collumnTemplates;

	public HttpPostOutput(String name, Matcher matcher, String url,
			List<String> collumnNames, List<String> collumnTemplates) throws MalformedURLException {
		super(name, matcher);
		this.url = new URL(url);

		this.collumnNames = collumnNames.toArray(new String[0]);
		this.collumnTemplates = new CompiledTemplate[collumnTemplates.size()];
		for (int i = 0; i < collumnTemplates.size(); i++) {
			this.collumnTemplates[i] = MVELUtil
					.compileTemplateTooled(collumnTemplates.get(i));
		}

	}

	@Override
	public void run() {
		Influxdb db;
		try {
			
			while (running) {
				Record r = queue.take();

				

				String[] data = new String[collumnTemplates.length];

				for (int i = 0; i < data.length; i++) {
					data[i] = TemplateRuntime.execute(this.collumnTemplates[i],
							r.getValues()).toString();
				}

				sendRequest(collumnNames, data);
				sent++;
			}
		} catch (UnknownHostException e) {

			err(getName() + " host not know", e);
		} catch (InterruptedException e) {
			// normal
		} catch (Exception e) {
			lost++;
			err(getName() + " pipe broken, going into reconnect", e);
			reconnect();
		}

	}

	public int sendRequest(String[] fields, String[] values)
			throws IOException {

		StringBuffer content = new StringBuffer();
		
		content.append("{");
		
		for (int i = 0; i < fields.length; i++) {
			if(i!=0)
				content.append(",");
			content.append("\"");
			content.append(fields[i]);
			content.append("\":\"");
			content.append(values[i]);
			content.append("\"");
		}
		
		content.append("}");
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		con.setRequestMethod("POST");

		con.setDoOutput(true);
		OutputStream wr = con.getOutputStream();
		wr.write(content.toString().getBytes());
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			// ignore Response content
			con.getInputStream().close();
		} else {
			throw new IOException("Server returned HTTP response code: "
					+ responseCode + "for URL: " + url + " with content :'"
					+ con.getResponseMessage() + "'");
		}
		return responseCode;

	}

}
