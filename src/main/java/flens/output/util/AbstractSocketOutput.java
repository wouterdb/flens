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
package flens.output.util;

import flens.core.Matcher;
import flens.core.Record;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class AbstractSocketOutput<WriterT> extends AbstractPumpOutput {

    protected int port;
    protected String host;
    protected Thread errPump;

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
    public AbstractSocketOutput(String name, String plugin, Matcher matcher, String server, int port) {
        super(name, plugin, matcher);
        this.port = port;
        this.host = server;
    }

    @Override
    public void run() {
        Socket socket = null;
        Record record = null;
        try {
            socket = new Socket(host, port);
            socket.setTcpNoDelay(true);
            WriterT br = getWriter(socket.getOutputStream());
            hookOnErrpump(socket);
            while (running) {
                record = queue.take();

                dispatch(br, record);

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
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    warn(getName() + "could not close socket", e);
                }
            }
        }

    }

    protected abstract WriterT getWriter(OutputStream outputStream);

    protected abstract void dispatch(WriterT outstream, Record record) throws IOException;

    protected void hookOnErrpump(Socket socket) {
        try {
            errPump = new Thread(new ErrPump(socket.getInputStream()));
        } catch (IOException e) {
            err(" err pipe setup failed", e);
        }

    }

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

    @Override
    public void stop() {
        super.stop();
        if (errPump != null) {
            errPump.interrupt();
        }
    }

}
