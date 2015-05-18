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

public class JsonEncoder extends AbstractConfig {

    @Override
    protected boolean isIn() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void construct() {
        String field = get("field", "json");
        List<String> fields = getArray("fields", null);
        List<String> exfields = getArray("exclude-fields", null);

        engine.addFilter(new flens.filter.JsonEncoder(name, plugin, tagger, matcher, prio, field, fields, exfields));
    }

    @Override
    protected boolean isOut() {
        return false;
    }

    @Override
    public String getDescription() {
        return "encode json messages";
    }

    @Override
    public List<Option> getOptions() {
        List<Option> out = new LinkedList<Option>(super.getOptions());
        out.add(new Option("field", "String", "json", "field in which to place json encode record"));
        out.add(new Option("fields", "[String]", "null", "fields in record to include in json, if empty use excludes"));
        out.add(new Option("exclude-fields", "[String]", "null",
                "fields to ignore when forming json, disabled by include"));
        return out;
    }
}
