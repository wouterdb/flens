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
import flens.core.Config.Option;

import java.util.LinkedList;
import java.util.List;



public class SystemOut extends AbstractConfig {

    @Override
    protected boolean isIn() {
        return false;
    }

    @Override
    protected void construct() {
        String field = get("field",null);
        engine.addOutput(new flens.output.SystemOut(name, plugin, matcher,field));
    }

    @Override
    protected boolean isOut() {
        return true;
    }

    @Override
    public String getDescription() {
        return "send logs to system out";
    }

    @Override
    public List<Option> getOptions() {
        List<Option> out = new LinkedList<Option>(super.getOptions());
        out.add(new Option("field", "String", "", "field to print, default is whole record"));
        return out;
    }
}
