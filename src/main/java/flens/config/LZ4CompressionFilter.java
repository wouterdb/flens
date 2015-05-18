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

import flens.config.util.AbstractConfig;

import java.util.LinkedList;
import java.util.List;

public class LZ4CompressionFilter extends AbstractConfig {

    @Override
    protected boolean isIn() {
        return false;
    }

    @Override
    protected void construct() {
        String outf = get("outfield", "body");
        String inf = get("infield", "json");

        boolean discard = getBool("discard", false);
        engine.addFilter(
                new flens.filter.LZ4CompressionFilter(name, plugin, tagger, matcher, prio, inf, outf, discard));

    }

    @Override
    protected boolean isOut() {
        return false;
    }

    @Override
    public String getDescription() {
        return "use lz4 to compress records, best used with batching";
    }

    @Override
    public List<Option> getOptions() {
        List<Option> out = new LinkedList<Option>(super.getOptions());
        out.add(new Option("outfield", "String", "", "field in which to place compressed data"));
        out.add(new Option("infield", "String", "", "field from which to get data"));
        out.add(new Option("discard", "boolean", "false", "discard original data"));
        return out;
    }
}
