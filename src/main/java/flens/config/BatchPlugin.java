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
package flens.config;

import flens.config.util.ActiveFilter;
import flens.filter.BatchingFilter;
import flens.input.util.InputQueueExposer;

import java.util.LinkedList;
import java.util.List;

public class BatchPlugin extends ActiveFilter {

    @Override
    public String getDescription() {
        return "plugin for packing togheter records in super records";
    }

    @Override
    protected void construct() {
        int interval = getInt("interval", 97);
        int maxbatch = getInt("maxbatch", 100);

        InputQueueExposer inpex = new InputQueueExposer(name + "_in", plugin, tagger);
        engine.addInput(inpex);

        engine.addOutput(new BatchingFilter(name, plugin, matcher, inpex, maxbatch, interval));

    }

    /**
     * @see flens.config.util.ActiveFilter#getOptions()
     */
    public List<Option> getOptions() {
        List<Option> out = new LinkedList<>(super.getOptions());
        out.add(new Option("interval", "int", "97", "maximal time to wait before a bacth is sent in ms"));
        out.add(new Option("maxbatch", "int", "100", "maximal nr of records per super record"));
        return out;
    }

}
