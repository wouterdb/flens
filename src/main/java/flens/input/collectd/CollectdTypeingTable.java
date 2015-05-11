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

package flens.input.collectd;

import static flens.util.ParseUtil.bool;
import static flens.util.ParseUtil.form;
import static flens.util.ParseUtil.isPlus;
import static flens.util.ParseUtil.list;
import static flens.util.ParseUtil.may;
import static flens.util.ParseUtil.nrHigh;
import static flens.util.ParseUtil.nrLow;

import flens.core.Constants;
import flens.core.Record;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CollectdTypeingTable extends TypeingTable {

    public CollectdTypeingTable() {
        loadExtendedDb();
    }

    private void loadExtendedDb() {
        try {
            InputStream is = getClass().getResourceAsStream("/collectd/typing.db");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            while (line != null) {
                if (!line.trim().startsWith("#")) {
                    parseLineForExt(line);
                }
                line = reader.readLine();
            }
        } catch (Exception e) {
            warn("could not read extended types for collectd", e);
        }
    }

    private void parseLineForExt(String line) {
        String[] parts = line.split("\\s+");
        // plugin type typeinstance name, unit type low high float
        if (parts.length < 9) {
            warn("bad line in typeing.db, to few parts" + parts.length, line);
            return;
        }
        try {
            if (parts.length == 9) {
                add(parts[0], may(parts[1]), may(parts[2]), list(parts[3]), parts[4], parts[5], form(parts[6]),
                        nrLow(parts[7]), nrHigh(parts[8]), false, isPlus(parts[2]));
            } else {
                add(parts[0], may(parts[1]), may(parts[2]), list(parts[3]), parts[4], parts[5], form(parts[6]),
                        nrLow(parts[7]), nrHigh(parts[8]), bool(parts[9]), isPlus(parts[2]));
            }
        } catch (IllegalArgumentException e) {
            warn("bad line in typeing.db", e);
        }
    }

    private void warn(String msg, String extra) {
        Logger.getLogger(getClass().getName()).warning(msg + " : " + extra);

    }

    private void warn(String msg, Exception excn) {
        Logger.getLogger(getClass().getName()).log(Level.WARNING, msg, excn);

    }

    /**
     * collectd has multi-valued metrics. The values are not self-descriptive
     */
    @SuppressWarnings("unchecked")
    public List<Record> expandAndDispatch(Record rec, boolean retime) {
        if (rec.getValues().containsKey("host")) {
            rec.setSource((String) rec.getValues().remove("host"));
        }

        if (retime) {
            rec.setTimestamp((long) ((Double) rec.getValues().remove("time") * 1000));
        }

        List<Number> values = (List<Number>) rec.getValues().get(CollectdConstants.VALUES);
        int size = values.size();

        Mapping typeing = resolve(rec);
        if (typeing == null) {
            return failAndDispatch(rec, "multi valued record not exanded");
        }

        if (size != typeing.getNames().length) {
            return failAndDispatch(rec, "multi valued record has unexpected number of values");

        }

        return normalizeAndDispatch(rec, values, typeing);

    }

    private List<Record> normalizeAndDispatch(Record rec, List<Number> values, Mapping typeing) {
        rec.getValues().remove(CollectdConstants.VALUES);
        rec = rec.doClone();

        // cut out collectd specific parts
        rec.getValues().remove(CollectdConstants.PLUGIN);
        Object instance = rec.getValues().remove(CollectdConstants.PLUGIN_INSTANCE);
        Object typeinstance = rec.getValues().remove(CollectdConstants.TYPE_INSTANCE);

        if (typeing.usetypeinstance) {
            if (instance != null && !instance.toString().isEmpty()) {
                err("plugin instance is set to " + instance + ", but should use type instance, " + typeinstance);
            }

            rec.getValues().put(Constants.INSTANCE, typeinstance);
            rec.getValues().put(Constants.TYPE, typeing.plugin);
        } else if (instance != null) {
            rec.getValues().put(Constants.INSTANCE, instance);
            rec.getValues().put(Constants.TYPE, typeing.plugin);
        } else {
            rec.getValues().remove(Constants.TYPE);
        }

        List<Record> outc = new LinkedList<Record>();

        for (int i = 0; i < typeing.names.length; i++) {
            Number value = values.get(i);
            Record out = rec.doClone();
            out.getValues().put(Constants.VALUE, value);
            out.getValues().put(Constants.METRIC, typeing.names[i]);

            out.addMeta(typeing.otype[i]);
            outc.add(out);
        }

        return outc;

    }

    private List<Record> failAndDispatch(Record rec, String msg) {
        err(msg + ":" + rec.toString());
        return Collections.singletonList(rec.doClone());
    }

    protected Logger logger = Logger.getLogger(getClass().getName());

    protected void err(String string) {
        logger.log(Level.SEVERE, string);
    }

}
