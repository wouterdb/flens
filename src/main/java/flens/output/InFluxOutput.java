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

import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;

import flens.core.Matcher;
import flens.core.Record;
import flens.output.util.AbstractPumpOutput;
import flens.util.MVELUtil;

public class InFluxOutput extends AbstractPumpOutput {


	protected int port = 8086;
	protected String host;
	protected String database;
	protected String user;
	protected String password;
	
	protected CompiledTemplate nameTemplate;
	protected String[] collumnNames;
	protected CompiledTemplate[] collumnTemplates;
	
	protected Influxdb db;

	
	public InFluxOutput(String name, String plugin, Matcher matcher,String server, int port,String database,String user, String password, String nameTemplate,List<String> collumnNames, List<String> collumnTemplates) {
		super(name,plugin,matcher);
		this.port = port;
		this.host = server;
		
		this.database = database;
		this.user = user;
		this.password = password;
		
		this.nameTemplate = MVELUtil.compileTemplateTooled(nameTemplate);
		this.collumnNames = collumnNames.toArray(new String[0]);
		this.collumnTemplates = new CompiledTemplate[collumnTemplates.size()];
		for (int i = 0; i < collumnTemplates.size(); i++) {
			this.collumnTemplates[i]=MVELUtil.compileTemplateTooled(collumnTemplates.get(i));
		}
		
	}

	
	@Override
	public void run() {
		Influxdb db;
		try {
			db = new Influxdb(host, port, database, user, password, TimeUnit.MILLISECONDS);
			while(running){
				Record r = queue.take();
				
				String metric = (String) TemplateRuntime.execute(this.nameTemplate, r.getValues());
				
				Object[] data = new Object[collumnTemplates.length];
				
				for (int i = 0; i < data.length; i++) {
					data[i] = TemplateRuntime.execute(this.collumnTemplates[i], r.getValues());
				}
				
				db.appendSeries("", metric, "", collumnNames, new Object[][] {data});
				db.sendRequest(true,false);	
				sent++;
			}
		} catch (UnknownHostException e) {
			
			err(getName()+ " host not know",e);
		} catch (InterruptedException e) {
			//normal
		} catch (Exception e) {
			lost++;
			err(getName()+ " pipe broken, going into reconnect",e);
			reconnect();
		} 
		
	}
	
	
}
