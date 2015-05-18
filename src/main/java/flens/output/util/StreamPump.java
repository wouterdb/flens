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
import flens.core.Output;
import flens.core.Record;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class StreamPump implements Output, Runnable {

    /**
     * @param name
     *            name under which this plugin is registered with the engine.
     * @param matcher
     *            matcher this output should used to select records
     */
    public StreamPump(String name, Matcher matcher) {
        super();
        this.name = name;
        this.matcher = matcher;
    }

    private String name;
    private Matcher matcher;
    protected BlockingQueue<Record> queue = new LinkedBlockingQueue<>();
    private Thread thread;
    protected volatile boolean running;

    @Override
    public Matcher getMatcher() {
        return matcher;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Queue<Record> getOutputQueue() {
        return queue;
    }

    @Override
    public void start() {
        thread = new Thread(this);
        running = true;
        thread.start();
    }

    @Override
    public void stop() {
        running = false;
        thread.interrupt();
    }

}
