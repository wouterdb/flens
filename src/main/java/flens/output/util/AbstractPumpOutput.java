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

package flens.output.util;

import flens.core.Flengine;
import flens.core.Matcher;
import flens.core.Output;
import flens.core.Record;
import flens.core.util.AbstractPlugin;


import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class AbstractPumpOutput extends AbstractPlugin implements Output, Runnable {

    /**
     * @param name
     *            name under which this plugin is registered with the engine
     * @param plugin
     *            name of config that loaded this plugin (as registered in
     *            plugins.json)
     * @param matcher
     *            matcher this output should used to select records
     */
    public AbstractPumpOutput(String name, String plugin, Matcher matcher) {
        super();
        this.name = name;
        this.plugin = plugin;
        this.matcher = matcher;
    }

    private String name;
    private String plugin;
    private Matcher matcher;
    protected BlockingQueue<Record> queue = new LinkedBlockingQueue<>();
    private Thread thread;
    protected volatile boolean running;
    protected volatile boolean reconnecting;

    protected int reconnectDelay = 10000;
    protected int flushOnSize = 10000;
    protected int sent;
    protected int lost;
   

    @Override
    public Matcher getMatcher() {
        return matcher;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPlugin() {
        return plugin;
    }

    @Override
    public Queue<Record> getOutputQueue() {
        return queue;
    }

    @Override
    public void start() {
        thread = new Thread(this);
        reconnecting = false;
        running = true;
        thread.start();
    }

    @Override
    public void stop() {
        running = false;
        thread.interrupt();
    }

    @Override
    public void join() throws InterruptedException {
        thread.join();
    }

    protected synchronized void reconnect() {
        // re-entrant
        if (reconnecting) {
            return;
        }
        reconnecting = true;
        // FIXME:may lose records
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (flushOnSize > 0 && getOutputQueue().size() > flushOnSize) {
                    lost += getOutputQueue().size();
                    getOutputQueue().clear();
                    warn("flushing queue to prevent overflow: " + getName());
                }
                try {
                    start();
                } catch (Exception e) {
                    err("reconnect failed", e);
                    reconnect();
                }
            }
        }, reconnectDelay);

    }

    @Override
    public int getRecordsLost() {
        return lost;
    }

    @Override
    public int getRecordsSent() {
        return sent;
    }

    protected byte[] getBytes(Object raw) {
        byte[] body;

        if (raw instanceof byte[]) {
            body = (byte[]) raw;
        } else if (raw instanceof String) {
            try {
                body = ((String) raw).getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                err("could not use utf-8!", e);
                body = ((String) raw).getBytes();
            }
        } else {
            try {
                body = raw.toString().getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                err("could not use utf-8!", e);
                body = raw.toString().getBytes();
            }
        }

        return body;
    }

    @Override
    public boolean canUpdateConfig() {
        return false;
    }

    @Override
    public void updateConfig(Flengine engine, Map<String, Object> tree) {
        throw new UnsupportedOperationException();

    }

   
}
