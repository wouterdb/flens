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

package flens.typing;

import static flens.util.ParseUtil.bool;
import static flens.util.ParseUtil.form;
import static flens.util.ParseUtil.nrHigh;
import static flens.util.ParseUtil.nrLow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TypeDb extends AbstractTypesDb<TypeDb> {

    protected Map<String, MetricType> types = new HashMap<>();

    /**
     * @throws IllegalArgumentException
     *             when type collides with existing type
     */
    public synchronized void add(MetricType type) {
        if (types.containsKey(type.getName())) {
            MetricType other = types.get(type.getName());
            if (!other.equals(type)) {
                throw new IllegalArgumentException("types do not correspond: " + type + " " + other);
            }
        } else {
            types.put(type.getName(), type);
        }
    }

    public synchronized MetricType get(String metric) {
        return types.get(metric);
    }

    public void writeOut(PrintWriter pw) {
        for (MetricType type : types.values()) {
            // name resource unit form low high [int]
            String line = String.format("%s %s %s %s %s %s", type.getName(), type.getResource(), type.getUnit(), type
                    .getForm().toShortString(), type.getMinValue().toString(), type.getMaxValue().toString());
            pw.println(line);
        }
    }

    public void parse(BufferedReader br, String filename) throws IOException {
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            String[] parts = line.split("\\s+");
            // name resource unit form low high [int]
            if (parts.length != 6 && parts.length != 7) {
                warn("bad line in types from " + filename + ", wrong number of parts" + parts.length, line);
                continue;
            }
            if (parts.length == 6) {
                add(new MetricType(parts[0], parts[2], parts[1], form(parts[3]), nrLow(parts[4]), nrHigh(parts[5]),
                        true));
            } else {
                add(new MetricType(parts[0], parts[1], parts[2], form(parts[3]), nrLow(parts[4]), nrHigh(parts[5]),
                        bool(parts[7])));
            }

        }

    }

  

    public Collection<MetricType> getAll() {
        return types.values();
    }

    @Override
    protected void addAll(TypeDb sub) {
        for (MetricType m : sub.getAll()) {
            add(m);
        }
    }

    @Override
    protected void clear() {
        types.clear();
        
    }

    @Override
    protected TypeDb createSub() {
        return new TypeDb();
    }

}
