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

package flens.config;

import flens.config.util.AbstractConfig;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SpecInput extends AbstractConfig {

    private static final String[] defaultspecs = { "cpu", "sleep" };

    @Override
    @SuppressWarnings("unchecked")
    protected void construct() {

        int interval = getInt("interval", 10000);
        List<String> specs = getArray("tests", Arrays.asList(defaultspecs));

        engine.addInput(new flens.input.SpecInput(name, plugin, tagger, interval, specs));
    }

    @Override
    protected boolean isIn() {
        return true;
    }

    @Override
    protected boolean isOut() {
        return false;
    }

    @Override
    public List<Option> getOptions() {
        List<Option> out = new LinkedList<Option>(super.getOptions());
        out.add(new Option("interval", "int", "10000", "interval between specs tests in ms"));
        out.add(new Option("tests", "[String]", Arrays.deepToString(defaultspecs), "test suites to run"));

        return out;
    }

    @Override
    public String getDescription() {
        return "Runs small tests to estimate machine performance";
    }

}
