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

import flens.core.Flengine;
import flens.core.Matcher;
import flens.core.Output;
import flens.core.Record;
import flens.core.util.AbstractPlugin;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class OutputQueueExposer extends AbstractPlugin implements Output {

    private Matcher matcher;
    private String name;
    private BlockingQueue<Record> queue = new LinkedBlockingQueue<>();

    /**
     * @param name
     *            name under which this plugin is registered with the engine.
     * @param matcher
     *            matcher this output should used to select records
     */
    public OutputQueueExposer(Matcher matcher, String name) {
        super();
        this.matcher = matcher;
        this.name = name;
    }

    private int sent;
    private int lost;

    @Override
    public Matcher getMatcher() {
        return matcher;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BlockingQueue<Record> getOutputQueue() {
        return queue;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public int getRecordsSent() {
        return sent;
    }

    @Override
    public int getRecordsLost() {
        return lost;
    }

    public void markSent() {
        sent++;
    }

    public void markLost() {
        lost++;
    }

    @Override
    public boolean canUpdateConfig() {
        return false;
    }

    @Override
    public void updateConfig(Flengine engine, Map<String, Object> tree) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPlugin() {
        return null;
    }

    @Override
    public void join() throws InterruptedException {

    }

}
