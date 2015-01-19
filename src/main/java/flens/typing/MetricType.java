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
     * @param name  @see getName()
     * @param unit  @see getUnit()
     * @param resource resource type to which this metric applies (cpu, interface, process,...) //todo: clarify 
     * @param form  form in which the data is reported @see MetricForm
     * @param minValue  minimal valid value
     * @param maxValue  maximal valid value
     * @param integer  is this an integer value?
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

}
