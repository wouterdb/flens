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
package flens.query;

import flens.core.Flengine;
import flens.core.Query;
import flens.core.QueryHandler;
import flens.core.util.AbstractPlugin;

import java.util.Map;

public class PingQuery extends AbstractPlugin implements QueryHandler {

    private String name;
    private String plugin;

    public PingQuery(String name, String plugin) {
        this.name = name;
        this.plugin = plugin;
    }

    @Override
    public boolean canHandle(Query query) {
        return query.getQuery().startsWith("ping://");
    }

    @Override
    public void handle(Query query) {
        query.respond("pong " + System.currentTimeMillis());

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void join() {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPlugin() {
        return plugin;
    }

    @Override
    public boolean canUpdateConfig() {
        return false;
    }

    @Override
    public void updateConfig(Flengine engine, Map<String, Object> tree) {

    }

}
