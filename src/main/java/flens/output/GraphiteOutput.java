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
import flens.output.util.AbstractSocketOutput;
import flens.util.MvelUtil;

import org.mvel2.UnresolveablePropertyException;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class GraphiteOutput extends AbstractSocketOutput<BufferedWriter> {

    private CompiledTemplate index;
    protected String metric;

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
    public GraphiteOutput(String name, String plugin, Matcher matcher, String server, int port, String metric) {
        super(name, plugin, matcher, server, port);

        this.metric = metric;

        this.index = MvelUtil.compileTemplateTooled(metric);
    }

    @Override
    protected void dispatch(BufferedWriter outstream, Record record) throws IOException {
        try {
            String metric = (String) TemplateRuntime.execute(this.index, record.getValues());
            Object value = record.getValues().get("value");
            if (value != null) {
                outstream.write(String.format("%s %s %d\n", metric, value, record.getTimestamp() / 1000));
                outstream.flush();
            }
        } catch (UnresolveablePropertyException e) {
            warn("could not form name for record " + record.toLine(),e);
            lost++;
            sent--;
        }

    }

    @Override
    protected BufferedWriter getWriter(OutputStream outputStream) {
        return new BufferedWriter(new OutputStreamWriter(outputStream));
    }

}
