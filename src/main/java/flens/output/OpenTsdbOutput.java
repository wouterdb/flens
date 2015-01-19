/*
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

import flens.core.Flengine;
import flens.core.Matcher;
import flens.core.Record;
import flens.output.util.AbstractSocketOutput;

import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class OpenTsdbOutput extends AbstractSocketOutput<BufferedWriter> {

    private Set<Pair<String, String>> sendTags = new HashSet<>();
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
     */
    public OpenTsdbOutput(String name, String plugin, Matcher matcher, String server, int port) {
        super(name, plugin, matcher,server,port);
    
    }

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
     * @param sendTags
     *            additional tags to send
     */
    public OpenTsdbOutput(String name, String plugin, Matcher matcher, String server, int port, List<String> sendTags) {
        super(name, plugin, matcher,server,port);
        for (String s : sendTags) {
            String[] parts = s.split(":", 2);
            if (parts.length == 2) {
                this.sendTags.add(Pair.of(parts[0], parts[1]));
            } else {
                this.sendTags.add(Pair.of(parts[0], parts[0]));
            }
        }
    }

    @Override
    public void writeConfig(Flengine engine, Map<String, Object> tree) {
        Map<String, Object> subtree = new HashMap<String, Object>();
        tree.put(getName(), subtree);
        subtree.put("plugin", getPlugin());

        subtree.put("host", host);
        subtree.put("port", port);
        getMatcher().outputConfig(subtree);

        List<String> sendTags = new LinkedList<>();

        for (Pair<String, String> tag : this.sendTags) {
            sendTags.add(tag.getKey() + ":" + tag.getValue());
        }
        subtree.put("send-tags", sendTags);

    }

    private String collectSendTags(Record record) {
        StringBuffer buffer = new StringBuffer();
        Map<String, Object> vals = record.getValues();
        for (Pair<String, String> names : sendTags) {
            Object value = vals.get(names.getLeft());
            if (value != null) {
                buffer.append(" ");
                buffer.append(names.getRight());
                buffer.append("=");
                buffer.append(value);
            }
        }
        return buffer.toString();
    }

    @Override
    protected BufferedWriter getWriter(OutputStream outputStream) {
        return new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    @Override
    protected void dispatch(BufferedWriter outstream, Record record) throws IOException {
        String suffix = collectSendTags(record);
        outstream.write(String.format("put %s %d %s host=%s%s\n", record.getValues().get("metric"),
                record.getTimestamp() / 1000, record.getValues().get("value"), record.getSource(), suffix));
        // System.out.println(String.format("put %s %d %s host=%s",r.getValues().get("metric"),
        // r.getTimestamp(),r.getValues().get("value"),r.getSource()));
        outstream.flush();
    }

  

}
