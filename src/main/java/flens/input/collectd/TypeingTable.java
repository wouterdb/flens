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

import flens.core.Constants;
import flens.core.Record;
import flens.typing.MetricForm;
import flens.typing.MetricType;

import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class TypeingTable {

    public static class Mapping {

        public Mapping(String plugin, String type, String typeInstance, String[] names, MetricType[] otype,
                boolean usetypeinstance) {
            super();
            this.plugin = plugin;
            this.type = type;
            this.typeInstance = typeInstance;
            this.names = names;
            this.otype = otype;

            this.usetypeinstance = usetypeinstance;
        }

        public String plugin;
        public String type;
        public String typeInstance;

        public boolean usetypeinstance;

        // for expansion
        public String[] names;

        public MetricType[] otype;

        public String getPlugin() {
            return plugin;
        }

        public String getType() {
            return type;
        }

        public String getTypeInstance() {
            return typeInstance;
        }

        public String[] getNames() {
            return names;
        }

        public MetricType[] getOutType() {
            return otype;
        }

        public boolean matches(String plugin, String type, String typeInstance) {
            if (!this.plugin.equals(plugin)) {
                return false;
            }
            if (this.type != null && !this.type.equals(type)) {
                return false;
            }
            if (this.typeInstance != null && !this.typeInstance.equals(typeInstance)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(names);
            result = prime * result + ((plugin == null) ? 0 : plugin.hashCode());
            result = prime * result + ((type == null) ? 0 : type.hashCode());
            result = prime * result + ((typeInstance == null) ? 0 : typeInstance.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Mapping other = (Mapping) obj;
            if (!Arrays.equals(names, other.names)) {
                return false;
            }
            if (plugin == null) {
                if (other.plugin != null) {
                    return false;
                }
            } else if (!plugin.equals(other.plugin)) {
                return false;
            }
            if (type == null) {
                if (other.type != null) {
                    return false;
                }
            } else if (!type.equals(other.type)) {
                return false;
            }
            if (typeInstance == null) {
                if (other.typeInstance != null) {
                    return false;
                }
            } else if (!typeInstance.equals(other.typeInstance)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Mapping {plugin=");
            builder.append(plugin);
            builder.append(", type=");
            builder.append(type);
            builder.append(", typeInstance=");
            builder.append(typeInstance);
            builder.append(", names=");
            builder.append(Arrays.toString(names));
            builder.append(", otype=");
            builder.append(Arrays.toString(otype));
            builder.append("}");
            return builder.toString();
        }

    }

    private MultiMap<String, Mapping> mappings = new MultiValueMap<>();

    public TypeingTable() {
        super();
    }

    /*
     * protected void add(String plugin, String type, String typeinstance,
     * MetricType... types) { String[] names = new String[types.length]; for
     * (int i = 0; i < names.length; i++) { names[i] = types[i].getName(); }
     * mappings.put(plugin, new Mapping(plugin, type, typeinstance, names,
     * types,false));
     * 
     * }
     */

    protected void add(String plugin, String type, String typeinstance, String[] names, String resource, String unit,
            MetricForm form, Number low, Number high, boolean isint, boolean useTypeInstance) {
        MetricType[] types = new MetricType[names.length];
        for (int i = 0; i < names.length; i++) {
            types[i] = new MetricType(names[i], unit, resource, form, low, high, isint);
        }
        mappings.put(plugin, new Mapping(plugin, type, typeinstance, names, types, useTypeInstance));

    }

    /**
     * get type instance for given collectd signature.
     */
    public Mapping resolve(String plugin, String type, String typeinstance) {
        @SuppressWarnings("unchecked")
        Collection<Mapping> mapping = (Collection<Mapping>) mappings.get(plugin);
        if (mapping == null) {
            return null;
        }
        // todo: make O(1)
        for (Mapping m : mapping) {
            if (m.matches(plugin, type, typeinstance)) {
                return m;
            }
        }
        return null;
    }

    /**
     * get type instance for given collectd record.
     */
    public Mapping resolve(Record rec) {
        return resolve((String) rec.getValues().get(CollectdConstants.PLUGIN),
                (String) rec.getValues().get(Constants.TYPE),
                (String) rec.getValues().get(CollectdConstants.TYPE_INSTANCE));
    }

    /**
     * linear search,....
     */
    @SuppressWarnings("unchecked")
    protected MetricType[] getForType(String type) {

        for (Mapping m : (Collection<Mapping>) (Object) mappings.values()) {
            if (m.type.equals(type)) {
                return m.otype;
            }
        }

        return new MetricType[0];
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Collection<MetricType> getTypes() {
        return new ArrayList<MetricType>((Collection) mappings.values());
    }

}