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

import java.util.Timer;
import java.util.TimerTask;

public abstract class AbstractPeriodicInput extends AbstractInput {

    protected Timer timer;
    protected int interval;

    public AbstractPeriodicInput(String name, String plugin, Tagger tagger, int interval) {
        super(name, plugin, tagger);
        this.interval = interval;
    }

    @Override
    public void start() {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(getWorker(), 0, interval);
    }

    protected abstract TimerTask getWorker();

    @Override
    public void stop() {
        timer.cancel();
    }

    @Override
    public void join() throws InterruptedException {

    }

}
