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

package flens.filter;

import flens.core.Constants;
import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;
import flens.typing.LogMatch;
import flens.typing.LogType;
import flens.typing.LogTypesDb;

import oi.thekraken.grok.api.Match;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LogTypeChecker extends AbstractFilter {

    protected String dir;
    protected boolean refresh;

    public LogTypeChecker(String name, String plugin, Matcher matcher, int prio, Tagger good, Tagger unknown,
            LogTypesDb db, String dir, boolean refresh) {
        super(name, plugin, good, matcher, prio);
        this.types = db;
        this.unknown = unknown;

        this.dir = dir;
        this.refresh = refresh;
    }

    private LogTypesDb types;
    private Tagger unknown;

    @Override
    public Collection<Record> process(Record in) {
        if (in.isLog()) {
            LogMatch adding = null;
            for (LogType lt : types.getAll()) {
                adding = lt.match(in);
                if (adding != null) {
                    break;
                }
            }

            if (adding == null) {
                unknown.adapt(in);
            } else {
                safemerge(in, adding);
                tag(in);
            }
        }
        return Collections.emptyList();
    }

    private void safemerge(Record in, LogMatch match) {

        Map<String, Object> parts = match.getValues();
        for (String key : parts.keySet()) {
            Object value = parts.get(key);
            safeset(match.getOwner(), in, key, value);
        }

        in.getValues().put(Constants.TYPE, match.getOwner().getName());
        in.getTags().addAll(match.getTags());

    }

    private boolean safeset(LogType logType, Record in, String name, Object value) {
        Object old = in.getValues().put(name, value);
        if (old == null) {
            return true;
        }
        if (value.equals(old)) {
            return true;
        } else {
            // we consider newer value to be more precise, s we replace
            //in.getValues().put(name, old);
            fine("colliding pattern match: rule named {0} attempts to set {1} to {2} but was {3}", logType.getName(),
                    name, value, old);
            return false;
        }

    }
}
