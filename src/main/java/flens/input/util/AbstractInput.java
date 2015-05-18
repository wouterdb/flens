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
import flens.core.Input;
import flens.core.Record;
import flens.core.Tagger;
import flens.core.util.AbstractPlugin;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

public abstract class AbstractInput extends AbstractPlugin implements Input {

    protected BlockingQueue<Record> in;
    protected Tagger tagger;
    protected String name;
    private String plugin;
    private int sent;

    /**
     * @param name
     *            name under which this plugin is registered with the engine
     * @param plugin
     *            name of config that loaded this plugin (as registered in
     *            plugins.json)
     * @param tagger
     *            tagger used to mark output records
     */
    public AbstractInput(String name, String plugin, Tagger tagger) {
        this.name = name;
        this.tagger = tagger;
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPlugin() {
        return plugin;
    }

    protected void dispatch(Record out) {
        tagger.adapt(out);
        in.add(out);
        sent++;
    }

    public void setInputQueue(BlockingQueue<Record> queue) {
        this.in = queue;
    }

    public int getRecordsSent() {
        return sent;
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
