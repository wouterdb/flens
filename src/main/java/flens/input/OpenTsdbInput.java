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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OpenTsdbInput extends AbstractListenerInput<BufferedReader> {

    private int port = 4242;

    public OpenTsdbInput(String name, String plugin, Tagger tagger, int port) {
        super(name, plugin, tagger);
        this.port = port;
    }

    @Override
    protected ServerSocket makeListener() throws IOException {
        return new ServerSocket(port);
    }

    @Override
    public BufferedReader getStream(Socket newSocket) throws IOException {
        return new BufferedReader(new InputStreamReader(newSocket.getInputStream()));
    }

    @Override
    public void readAndProcess(BufferedReader in) throws IOException {
        String line = in.readLine();
        if (line == null) {
            throw new IOException("connection lost");
        }

        try (Scanner st = new Scanner(line);) {
            if (!st.next().equals("put")) {
                warn("bad line", line);
                return;
            }

            final String metricName = st.next();
            final long time = st.nextLong();
            final String metric = st.next();

            Map<String, Object> values = new HashMap<String, Object>();

            while (st.hasNext()) {
                String tag = st.next();
                String[] parts = tag.split("=");
                values.put(parts[0], parts.length > 1 ? parts[1] : "");
            }

            String host = (String) values.remove("host");

            if (host == null) {
                host = "UNKNOW";
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "tsdb metric has no host tag: " + line);
            }
            values.put(Constants.METRIC, metricName);
            values.put(Constants.VALUE, metric);

            Record out = Record.forTransport(time * 1000, host, values);
            dispatch(out);
        } catch (NoSuchElementException e) {
            warn("line too short", line);
        }

    }

    private void warn(String msg, String line) {
        Logger.getLogger(getClass().getName()).log(Level.WARNING, "opentsdb: " + msg + " [ " + line + "]");

    }

    @Override
    public void tearDown(BufferedReader in2) throws IOException {
        in2.close();

    }

}
