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
package flens.input.util;

import flens.core.Flengine;
import flens.core.Record;
import flens.core.Tagger;

import java.util.Map;

public class InputQueueExposer extends AbstractInput {

    private boolean stopped;

    public InputQueueExposer(String name, String plugin, Tagger tagger) {
        super(name, plugin, tagger);
    }

    public void send(Record rec) {
        dispatch(rec);
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        stopped = true;
    }

    @Override
    public void join() throws InterruptedException {
    }

    public boolean isStopped() {
        return stopped;
    }

    @Override
    public void writeConfig(Flengine engine, Map<String, Object> tree) {
    }

}
