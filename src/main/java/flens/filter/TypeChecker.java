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
import flens.typing.MetricType;
import flens.typing.TypeDb;

import java.util.Collection;
import java.util.Collections;

public class TypeChecker extends AbstractFilter {

    private final boolean checkall;

    public TypeChecker(String name, String plugin, Matcher matcher, int prio, Tagger good, Tagger bad, Tagger untyped,
            Tagger unknown, TypeDb db, boolean checkall) {
        super(name, plugin, good, matcher, prio);
        this.untyped = untyped;
        this.bad = bad;
        this.types = db;
        this.unknown = unknown;
        this.checkall = checkall;
    }

    private TypeDb types;
    private Tagger untyped;
    private Tagger bad;
    private Tagger unknown;

    @Override
    public Collection<Record> process(Record in) {
        if (checkall || in.isMetric()) {
            String metric = (String) in.get(Constants.METRIC);
            MetricType type = types.get(metric);
            if (type != null) {
                setType(in, type);
            } else {
                unknown(in);
                if (!in.hasMetricType()) {
                    untyped(in);
                } else {
                    typed(in);
                }
            }
        }
        return Collections.emptyList();
    }

    private void untyped(Record in) {
        if (untyped != null) {
            untyped.adapt(in);
        } else {
            warn("untyped record: " + in);
        }

    }

    private void unknown(Record in) {
        if (unknown != null) {
            unknown.adapt(in);
        } else {
            warn("unknown type: " + in.get(Constants.METRIC));
        }

    }

    private void bad(Record in) {
        if (bad != null) {
            bad.adapt(in);
        } else {
            warn("bad record, wrong type: " + in);
        }

    }

    private void typed(Record in) {
        tag(in);
    }

    private void setType(Record in, MetricType type) {
        // not untyped, not unknown
        // typed
        typed(in);

        boolean good = hasOrSet(in, Constants.UNIT, type.getUnit());
        good &= hasOrSet(in, Constants.RESCOURCE_TYPE, type.getResource());
        good &= hasOrSet(in, Constants.FORM, type.getForm().toShortString());
        good &= hasOrSet(in, Constants.RANGE, type.getRange());

        if (!good) {
            bad(in);
        }

    }

    private boolean hasOrSet(Record in, String name, String value) {
        Object old = in.getValues().put(name, value);
        if (old == null) {
            return true;
        }
        if (value.equals(old)) {
            return true;
        } else {
            // for safetype, be conservative
            in.getValues().put(name, old);
            return false;
        }

    }
}
