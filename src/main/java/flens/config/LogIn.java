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
import flens.input.FlensLogHandler;
import flens.input.util.InputQueueExposer;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class LogIn extends AbstractConfig {

    @Override
    protected boolean isIn() {
        return true;
    }

    @Override
    protected void construct() {
        String level = get("level", Level.INFO.toString());
        // String filter = get("filter",".*");

        InputQueueExposer exp = new InputQueueExposer(name, plugin, tagger);
        FlensLogHandler lh = new flens.input.FlensLogHandler(exp);

        lh.setLevel(Level.parse(level));
        // lh.setFilter(Filter.);

        LogManager.getLogManager().getLogger("").addHandler(lh);

        engine.addInput(exp);
    }

    @Override
    protected boolean isOut() {
        return false;
    }

    @Override
    public List<Option> getOptions() {
        List<Option> out = new LinkedList<Option>(super.getOptions());
        out.add(new Option("level", "String", Level.INFO.toString(), "log level"));
        return out;
    }

    @Override
    public String getDescription() {
        return "connect the java root logger to flens";
    }

}
