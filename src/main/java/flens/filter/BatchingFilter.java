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

package flens.filter;

import flens.core.Matcher;
import flens.core.Record;
import flens.input.util.InputQueueExposer;
import flens.output.util.AbstractPeriodicOutput;

import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

public class BatchingFilter extends AbstractPeriodicOutput {

    public class TransmitTask extends TimerTask {

        @Override
        public void run() {
            transmit();
        }

    }

    private int maxbatch;

    private List<Record> buffer = new LinkedList<>();

    /**
     * Filter for collecting multiple records. Input are collected until
     * maxbatch records are collected or maxtime has elapsed since the fist
     * batch.
     * 
     * @param name
     *            name under which this plugin is registered with the engine
     * @param plugin
     *            name of config that loaded this plugin (as registered in
     *            plugins.json)
     * @param matcher
     *            matcher this filter should used to select recrods
     * @param inexp
     *            the input queue exposer to which this plugin pots its output
     * @param maxbatch
     *            maximal number of records per batch
     * @param maxtime
     *            maximal time between subsequent batches
     */
    public BatchingFilter(String name, String plugin, Matcher matcher, InputQueueExposer inexp, int maxbatch,
            int maxtime) {
        super(name, plugin, matcher, maxtime, inexp);
        this.maxbatch = maxbatch;

    }

    @Override
    public synchronized void process(Record in) {
        buffer.add(in);
        if (buffer.size() >= maxbatch) {
            transmit();
        } else if (tt == null) {
            restartTimer();
        }

    }

    private synchronized void transmit() {
        tt.cancel();
        tt = null;

        Record rec = Record.pack(buffer);
        buffer = new LinkedList<>();
        in.send(rec);
    }

    @Override
    protected TimerTask createTimerTask() {
        return new TransmitTask();
    }

}
