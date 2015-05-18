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
package flens.typing;

import org.apache.commons.lang3.tuple.Pair;

public class MetricType {

    private String name;
    private String unit;

    private String resource;

    private MetricForm form;

    private Number minValue;
    private Number maxValue;

    private boolean integer;

    /**
     * Create metric type.
     * 
     * @param name
     * @see getName()
     * @param unit
     * @see getUnit()
     * @param resource
     *            resource type to which this metric applies (cpu, interface,
     *            process,...) //todo: clarify
     * @param form
     *            form in which the data is reported @see MetricForm
     * @param minValue
     *            minimal valid value
     * @param maxValue
     *            maximal valid value
     * @param integer
     *            is this an integer value?
     */
    public MetricType(String name, String unit, String resource, MetricForm form, Number minValue, Number maxValue,
            boolean integer) {
        super();
        this.name = name;
        this.unit = unit;
        this.form = form;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.integer = integer;
        this.resource = resource;
    }

    /**
     * name of the record type
     * <p/>
     * convention: for raw metric: agent.resource.sub (collectd.cpu.idle) for
     * normalized metrics: scope.resource.sub (sys.cpu.idle)
     */
    public String getName() {
        return name;
    }

    /**
     * Whenever a volume is to be measured, SI approved units and their approved
     * symbols or abbreviations should be used. Information units should be
     * expressed in bits (‘b’) or bytes (‘B’). For a given meter, the units
     * should NEVER, EVER be changed. When the measurement does not represent a
     * volume, the unit description should always described WHAT is measured
     * (ie: apples, disk, routers, floating IPs, etc.).
     */
    public String getUnit() {
        return unit;
    }

    public MetricForm getForm() {
        return form;
    }

    public Number getMinValue() {
        return minValue;
    }

    public Number getMaxValue() {
        return maxValue;
    }

    public boolean isInteger() {
        return integer;
    }

    public String getResource() {
        return resource;
    }

    /**
     * @return The interval in which this value is valid.
     */
    public String getRange() {
        String start = Double.isInfinite(minValue.doubleValue()) ? "]," : "[" + minValue + ",";
        String end = Double.isInfinite(maxValue.doubleValue()) ? "[" : maxValue + "]";
        return start + end;
    }

    public static Pair<Number, Number> parseRange(String range) {
        String[] parts = range.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("range not in interval form " + range);
        }

        try {

            Number low;
            if (parts[0].startsWith("]")) {
                low = Double.NEGATIVE_INFINITY;
            } else {
                String lows = parts[0].substring(1);
                if (lows.contains(".")) {
                    low = Double.parseDouble(lows);
                } else {
                    low = Long.parseLong(lows);
                }
            }

            Number high;
            if (parts[1].endsWith("[")) {
                high = Double.POSITIVE_INFINITY;
            } else {
                String highs = parts[1].substring(0, parts[1].length() - 1);
                if (highs.contains(".")) {
                    high = Double.parseDouble(highs);
                } else {
                    high = Long.parseLong(highs);
                }
            }

            return Pair.of(low, high);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("range not in interval form " + range, e);
        }

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((form == null) ? 0 : form.hashCode());
        result = prime * result + (integer ? 1231 : 1237);
        result = prime * result + ((maxValue == null) ? 0 : maxValue.hashCode());
        result = prime * result + ((minValue == null) ? 0 : minValue.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((resource == null) ? 0 : resource.hashCode());
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
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
        MetricType other = (MetricType) obj;
        if (form != other.form) {
            return false;
        }
        if (integer != other.integer) {
            return false;
        }
        if (maxValue == null) {
            if (other.maxValue != null) {
                return false;
            }
        } else if (!maxValue.equals(other.maxValue)) {
            return false;
        }
        if (minValue == null) {
            if (other.minValue != null) {
                return false;
            }
        } else if (!minValue.equals(other.minValue)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (resource == null) {
            if (other.resource != null) {
                return false;
            }
        } else if (!resource.equals(other.resource)) {
            return false;
        }
        if (unit == null) {
            if (other.unit != null) {
                return false;
            }
        } else if (!unit.equals(other.unit)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("[Metric type: %s %s %s %s %s %s]", getName(), getResource(), getUnit(), getForm()
                .toShortString(), getMinValue().toString(), getMaxValue().toString());
    }

}
