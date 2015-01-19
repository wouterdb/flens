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

package flens.input.util;

import flens.core.Tagger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class AbstractProcessPoller extends AbstractInput {

    protected String cmd;
    protected Process proc;
    protected List<String> args;
    protected Timer timer;
    protected long period;
    protected boolean running;
    protected LinkedList<String> fullArgs;

    /**
      * @param name
     *            name under which this plugin is registered with the engine
     * @param plugin
     *            name of config that loaded this plugin (as registered in
     *            plugins.json)
     * @param taggr
     *            tagger used to mark output records (one line becomes one
     *            record)
     
     * @param cmd
     *            command to execute
     * @param args
     *            arguments to the command
     * @param period
     *            polling period
     */
    public AbstractProcessPoller(String name, String plugin, Tagger taggr, String cmd, List<String> args, long period) {
        super(name, plugin, taggr);
        this.cmd = cmd;
        this.args = new LinkedList<>(args);
        this.period = period;
        this.fullArgs = new LinkedList<>(args);
        this.fullArgs.add(0, cmd);
    }

    synchronized void poll() throws InterruptedException {

        ProcessBuilder pb = new ProcessBuilder(fullArgs);
        try {
            proc = pb.start();
        } catch (IOException e) {
            err("could not start process", e);
            return;
        }

        captureStreams();

        proc.waitFor();

        postRun();
        running = false;
        proc = null;
        notify();
    }

    protected abstract void captureStreams();

    protected abstract void postRun() throws InterruptedException;

    @Override
    public synchronized void stop() {
        timer.cancel();
        if (proc != null) {
            proc.destroy();
        }
    }

    @Override
    public synchronized void join() throws InterruptedException {
        while (running) {
            wait();
        }
    }

    @Override
    public synchronized void start() {
        running = true;
        timer = new Timer(getName());
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    poll();
                } catch (InterruptedException e) {
                    err("failed to poll", e);
                }

            }
        }, 0, period);

    }

}
