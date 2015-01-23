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

import java.util.LinkedList;
import java.util.List;

import flens.config.util.AbstractConfig;
import flens.core.Config.Option;
import flens.core.Tagger;
import flens.typing.DefaultTypeDb;

public class MetricTypeChecker extends AbstractConfig {
    @Override
    protected boolean isIn() {
        return false;
    }

    @Override
    protected boolean isOut() {
        return false;
    }

    @Override
    protected void construct() {
        Tagger untyped = readTagger("untyped-");
        Tagger unknown = readTagger("unknown-");
        Tagger bad = readTagger("bad-");

        if (untyped == Tagger.empty) {
            untyped = null;
        }

        if (unknown == Tagger.empty) {
            unknown = null;
        }

        if (bad == Tagger.empty) {
            bad = null;
        }

        boolean checkall = getBool("checkall", false);

        engine.addFilter(new flens.filter.TypeChecker(name, plugin, matcher, prio, tagger, bad, untyped, unknown,
                new DefaultTypeDb(), checkall));

    }

    @Override
    public String getDescription() {
        return "check types seen against database of types." + " All types are expanded to full type info. "
                + " Unknow types, untyped records and records with type information different"
                + " form the database can be detected and each tagged differently."
                + " If no taggers are specified, bad, unknown and untyped records are logged";
    }

    @Override
    public List<Option> getOptions() {
        List<Option> out = new LinkedList<Option>(super.getOptions());
        out.add(new Option("untyped-add-tags", "[String]", "[]",
                "add following tags to records wich have no or incomplete type information after processing"));
        out.add(new Option("untyped-remove-tags", "[String]", "[]",
                "add following tags from records wich have no or incomplete type information after processing"));
        out.add(new Option("untyped-set-type", "String", "",
                "add following type to records wich have no or incomplete type information after processing"));

        out.add(new Option("unknown-add-tags", "[String]", "[]",
                "add following tags to records with unknown type"));
        out.add(new Option("unknown-remove-tags", "[String]", "[]", 
                "add following tags from records with unknown type"));
        out.add(new Option("unknown-set-type", "String", "", 
                "add following type to records with unknown type"));

        out.add(new Option("bad-add-tags", "[String]", "[]",
                "add following tags to records which come into the system with a bad type"));
        out.add(new Option("bad-remove-tags", "[String]", "[]",
                "add following tags from records which come into the system with a bad type"));
        out.add(new Option("bad-set-type", "String", "",
                "add following type to records which come into the system with a bad type"));

        out.add(new Option("checkall", "boolean", "false", "check all records, also if they have no metric field"));

        return out;
    }
}
