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
import flens.input.util.InputQueueExposer;

import java.util.Timer;
import java.util.TimerTask;

public abstract class AbstractPeriodicOutput extends AbstractPumpOutput {

    private int interval;
    protected InputQueueExposer in;
    private Timer timer = new Timer();
    protected TimerTask tt;

    /**
     * @param name
     *            name under which this plugin is registered with the engine
     * @param plugin
     *            name of config that loaded this plugin (as registered in
     *            plugins.json)
     * @param matcher
     *            matcher this filter should used to select recrods
     * @param inpex
     *            the input queue exposer to which this plugin pots its output
     * @param interval
     *           time before output is generated
     *            
     */
    public AbstractPeriodicOutput(String name, String plugin, Matcher matcher, int interval, InputQueueExposer inpex) {
        super(name, plugin, matcher);
        this.interval = interval;
        this.in = inpex;
    }

    @Override
    public void start() {
        super.start();
        restartTimer();
    }

    protected synchronized void restartTimer() {
        if (tt != null) {
            tt.cancel();
        }
        tt = createTimerTask();
        timer.schedule(tt, interval, interval);
    }

    protected abstract TimerTask createTimerTask();

    @Override
    public void run() {
        try {
            while (true) {
                Record record = queue.take();
                try {
                    process(record);
                    sent++;
                } catch (Exception e) {
                    err("fault in script", e);
                    lost++;
                    reconnect();
                }
            }
        } catch (InterruptedException e) {
            // normal for stop
            stop();
        }
    }

    protected abstract void process(Record in);
}
