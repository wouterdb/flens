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

import flens.core.Matcher;
import flens.core.Record;
import flens.output.util.AbstractSocketOutput;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;



public class SocketOutput extends AbstractSocketOutput<DataOutputStream> {

    private String field;

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
     * @param field
     *            name of the field that is to be sent over the socket
     */
    public SocketOutput(String name, String plugin, Matcher matcher, String server, int port, String field) {
        super(name, plugin, matcher, server, port);
        this.field = field;
    }

    @Override
    protected DataOutputStream getWriter(OutputStream outputStream) {
        return new DataOutputStream(outputStream);
    }

    @Override
    protected void dispatch(DataOutputStream br, Record record) throws IOException {
        Object value = record.getValues().get(field);
        byte[] bx = getBytes(value);
        br.writeInt(bx.length);
        br.write(bx);
        br.flush();
    }

}
