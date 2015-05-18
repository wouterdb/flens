/*
 *
 *     Copyright 2013-2015 KU Leuven Research and Development - iMinds - Distrinet
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

import flens.core.Matcher;
import flens.core.Record;
import flens.output.util.AbstractPumpOutput;
import flens.util.MvelUtil;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;

import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    /**
     * @param name
     *            name under which this plugin is registered with the engine
     * @param plugin
     *            name of config that loaded this plugin (as registered in
     *            plugins.json)
     * @param matcher
     *            matcher this output should used to select records
     * @param server
     *            hostname of the AMQP server
     * @param port
     *            TCP port to connect to
     * @param user
     *            username to use
     * @param password
     *            password to use
     * @param database
     *            database to conect to
     * @param nameTemplate
     *            template to construct the metric name
     * @param collumnNames
     *            names of the collumn sent to influx
     * @param collumnTemplates
     *            mvel templates to create values for those collumns
     */
    public InFluxOutput(String name, String plugin, Matcher matcher, String server, int port, String database,
            String user, String password, String nameTemplate,
            List<String> collumnNames, List<String> collumnTemplates) {
        super(name, plugin, matcher);
        this.port = port;
        this.host = server;

        this.database = database;
        this.user = user;
        this.password = password;

        this.nameTemplate = MvelUtil.compileTemplateTooled(nameTemplate);
        this.collumnNames = collumnNames.toArray(new String[0]);
        this.collumnTemplates = new CompiledTemplate[collumnTemplates.size()];
        for (int i = 0; i < collumnTemplates.size(); i++) {
            this.collumnTemplates[i] = MvelUtil.compileTemplateTooled(collumnTemplates.get(i));
        }

    }

    @Override
    public void run() {
        Influxdb db;
        try {
            db = new Influxdb(host, port, database, user, password, TimeUnit.MILLISECONDS);
            while (running) {
                Record in = queue.take();

                String metric = (String) TemplateRuntime.execute(this.nameTemplate, in.getValues());

                Object[] data = new Object[collumnTemplates.length];

                for (int i = 0; i < data.length; i++) {
                    data[i] = TemplateRuntime.execute(this.collumnTemplates[i], in.getValues());
                }

                db.appendSeries("", metric, "", collumnNames, new Object[][] { data });
                db.sendRequest(true, true);
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

}
