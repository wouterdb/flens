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

import flens.core.Tagger;
import flens.input.util.AbstractProcessPoller;
import flens.input.util.StreamPump;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class ProcessPoller extends AbstractProcessPoller {

    private StreamPump out;
    private StreamPump err;
    private Tagger outT;
    private Tagger errT;

    /**
     * @param name
     *            name under which this plugin is registered with the engine
     * @param plugin
     *            name of config that loaded this plugin (as registered in
     *            plugins.json)
     * @param out
     *            tagger used to mark output records (one line becomes one
     *            record)
     * @param err
     *            tagger used to mark records of the error stream
     * @param cmd
     *            command to execute
     * @param args
     *            arguments to the command
     * @param period
     *            polling period
     */
    public ProcessPoller(String name, String plugin, Tagger out, Tagger err, String cmd, 
            List<String> args, long period) {
        super(name, plugin, null, cmd, args, period);
        this.outT = out;
        this.errT = err;
    }

    @Override
    protected void captureStreams() {
        out = new StreamPump(getName() + ".out", getPlugin(), outT, new BufferedReader(new InputStreamReader(
                proc.getInputStream())));
        err = new StreamPump(getName() + ".err", getPlugin(), errT, new BufferedReader(new InputStreamReader(
                proc.getErrorStream())));
        out.setInputQueue(in);
        err.setInputQueue(in);
        out.start();
        err.start();

    }

    @Override
    protected void postRun() throws InterruptedException {
        out.join();
        err.join();
        out = null;
        err = null;

    }

}
