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

package flens.core;

import flens.core.util.AllMatcher;

public class Constants {

    // obligatory fields
    /**
     * time in ms since epoch (unix timestamp).
     * <p/><b>Obligatory</b>
     */
    public static final String TIME = "time";
    
    /**
     * Hostname of machine to which the record appertains.
     * <p/><b>Obligatory</b>
     */
    public static final String SOURCE = "source";
    /** 
     * primary name of the metric.
     * <p/><b>Obligatory for metrics</b>
     */
    public static final String METRIC = "metric";

    /** 
     * Primary name of the metric.
     * 
     * <p/><b>Optional for logs</b>
     */
    public static final String LOG_FILE = "file";

    // primary value fields

    /**
     * text message.
     * 
     * <p/><b>Primary value field for logs</b>
     * 
     * 
     */
    public static final String L_MESSAGE = "message";
    public static final String MESSAGE = "message";

    /**
     * binary message.
     * 
     * <p/><b>Primary value field for binary messages</b>
     */
    public static final String BINARY_MESSAGE = "body";
    public static final String BODY = "body";

    /**
     * numerical value message.
     * 
     * <p/><b>Primary value field for metrics</b>
     */
    public static final String VALUE = "value";

    public static final Matcher ALLMATCHER = new AllMatcher();

    public static final String INTERVAL = "interval";
    public static final String SEVERITY = "severity";

    public static final String SUBRECORDS = "subrecords";
    public static final String UNIT = "unit";
    public static final String TYPE = "type";
    public static final String INSTANCE = "instance";

    public static final String TAGS = "tags";

}
