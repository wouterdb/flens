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
        
package flens.input;

import flens.core.Constants;
import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractListenerInput;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SocketInput extends AbstractListenerInput<Pair<String, DataInputStream>> {

    private int port;

    public SocketInput(String name, String plugin, Tagger tagger, int port) {
        super(name, plugin, tagger);
        this.port = port;
    }

    @Override
    protected ServerSocket makeListener() throws IOException {
        return new ServerSocket(port);
    }

    @Override
    public Pair<String, DataInputStream> getStream(Socket newSocket) throws IOException {
        String hostname = newSocket.getInetAddress().getHostName();
        return Pair.of(hostname, new DataInputStream(newSocket.getInputStream()));
    }

    @Override
    public void readAndProcess(Pair<String, DataInputStream> inx) throws IOException {
        DataInputStream in = inx.getRight();
        final String host = inx.getLeft();
        int line = in.readInt();
        if (line > 65536) {
            throw new IOException("number of bytes to be read is too big: " + line);
        }

        byte[] block = new byte[line];

        IOUtils.readFully(in, block);

        Map<String, Object> values = new HashMap<String, Object>();

        values.put(Constants.BODY, block);
        Record out = Record.forTransport(System.currentTimeMillis(), host, values);
        dispatch(out);

    }

    @Override
    public void tearDown(Pair<String, DataInputStream> in2) throws IOException {
        in2.getRight().close();
    }

}
