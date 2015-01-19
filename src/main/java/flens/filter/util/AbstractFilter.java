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

package flens.filter.util;

import flens.core.Filter;
import flens.core.Flengine;
import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.core.util.AbstractPlugin;

import java.util.Map;

public abstract class AbstractFilter extends AbstractPlugin implements Filter {

    private Tagger tagger;
    private Matcher matcher;
    private String name;
    private String plugin;
    private int prio;

    /**
     * @param name
     *            name under which this plugin is registered with the engine
     * @param plugin
     *            name of config that loaded this plugin (as registered in
     *            plugins.json)
     * @param tagger 
     *            tagger used to mark output records
     * @param matcher
     *            matcher this filter should used to select recrods
     * @param prio
     *            plugin priority
     */
    public AbstractFilter(String name, String plugin, Tagger tagger, Matcher matcher, int prio) {
        this.name = name;
        this.tagger = tagger;
        this.matcher = matcher;
        this.prio = prio;
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

    protected Record tag(Record out) {
        tagger.adapt(out);
        return out;
    }

    @Override
    public Matcher getMatcher() {
        return matcher;
    }

    @Override
    public int priority() {
        return prio;
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
