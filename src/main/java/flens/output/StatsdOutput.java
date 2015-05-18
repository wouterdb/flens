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

import org.mvel2.UnresolveablePropertyException;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class StatsdOutput extends AbstractPumpOutput {

    private CompiledTemplate index;
    protected String metric;
    protected String server;
    protected int port;
    private ByteArrayOutputStream outbytes;
    private BufferedWriter outstream;
    private InetSocketAddress dest;

    /**
     * @param name
     *            name under which this plugin is registered with the engine
     * @param plugin
     *            name of config that loaded this plugin (as registered in
     *            plugins.json)
     * @param matcher
     *            matcher this output should used to select records
     * @param server
     *            hostname of the graphite server
     * @param port
     *            TCP port to connect to
     * @param metric
     *            mvel template to create flat metric name
     */
    public StatsdOutput(String name, String plugin, Matcher matcher, String server, int port, String metric) {
        super(name, plugin, matcher);

        this.metric = metric;

        this.index = MvelUtil.compileTemplateTooled(metric);
        this.server = server;
        this.port = port;

        this.dest = new InetSocketAddress(server, port);
        outbytes = new ByteArrayOutputStream();
        outstream = new BufferedWriter(new OutputStreamWriter(outbytes));

    }

    @Override
    public void run() {
        try {
            while (running) {

                Record record = queue.take();
                try (DatagramSocket out = new DatagramSocket()) {

                    try {
                        outbytes.reset();
                        String metric = (String) TemplateRuntime.execute(this.index, record.getValues());
                        if (metric != null) {
                            outstream.write(String.format("%s", metric));
                            outstream.flush();
                        }
                        DatagramPacket pd = new DatagramPacket(outbytes.toByteArray(), outbytes.size(), dest);
                        out.send(pd);
                    } catch (UnresolveablePropertyException e) {
                        warn("could not form name for record " + record.toLine(), e);
                        lost++;
                        sent--;
                    }

                    sent++;
                }
            }

        } catch (InterruptedException e) {
            // normal for stop
            stop();
        } catch (IOException e) {
            err("Statsd pipe broken", e);
            stop();
            lost++;
            reconnect();
        }
    }

}
