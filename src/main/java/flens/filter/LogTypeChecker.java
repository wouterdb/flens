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
    private boolean breakOnMatch;

    public LogTypeChecker(String name, String plugin, Matcher matcher, int prio, Tagger good, Tagger unknown,
            LogTypesDb db, String dir, boolean refresh, boolean breakOnMatch) {
        super(name, plugin, good, matcher, prio);
        this.types = db;
        this.unknown = unknown;

        this.dir = dir;
        this.refresh = refresh;
        this.breakOnMatch = breakOnMatch;
    }

    private LogTypesDb types;
    private Tagger unknown;

    @Override
    public Collection<Record> process(Record in) {
        if (in.isLog()) {
            String message = (String) in.get(Constants.MESSAGE);
            List<Pair<LogType, Match>> matches = new LinkedList<>();
            for (LogType lt : types.getAll()) {
                Match adding = lt.match(message);
                adding.captures();
                if (!adding.isNull()) {
                    matches.add(Pair.of(lt, adding));
                    if (breakOnMatch) {
                        break;
                    }
                }
            }

            if (matches.isEmpty()) {
                unknown.adapt(in);
            } else {
                safemerge(in, matches);
                tag(in);
            }
        }
        return Collections.emptyList();
    }

    private void safemerge(Record in, List<Pair<LogType, Match>> matches) {
        List<String> names = new LinkedList<>();
        for (Pair<LogType, Match> match : matches) {
            Map<String, Object> parts = match.getRight().toMap();
            for (String key : parts.keySet()) {
                Object value = parts.get(key);
                safeset(match.getKey(), in, key, value);
            }
            names.add(match.getKey().getType());
        }

        in.getValues().put(Constants.TYPE, names);

    }

    private boolean safeset(LogType logType, Record in, String name, Object value) {
        Object old = in.getValues().put(name, value);
        if (old == null) {
            return true;
        }
        if (value.equals(old)) {
            return true;
        } else {
            // for safety, be conservative
            in.getValues().put(name, old);
            warn("colliding pattern match: rule named {0} attempts to set {1} to {2} but was {3}", logType.getName(),
                    name, value, old);
            return false;
        }

    }
}
