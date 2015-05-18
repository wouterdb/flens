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
        
package flens.core;

import java.util.Collection;

/**
 * A plugin that modifies individual records.
 * All filters form a chain 
 * <p/>
 * All filters must be thread safe
 *
 */
public interface Filter extends Plugin {

    /**
     * Process a record. 
     * <p/>
     * Each filter can do one of the following. 
     * <ul>
     * <li> Make changes to the input record. The changed record is passed to the next filter.</li> 
     * <li> New records can be return. New records are fed to the start of the filter chain. </li>
     * <li> If the type of the input record is set to null, it is discarded </li>
     * </ul> 
     */
    public Collection<Record> process(Record in);

    /**
     * The Matcher determines to which records this plugin is applied. 
     * <p/>
     * idempotent, fast.
     */
    public Matcher getMatcher();

    /**
     * Plugins are executed in order of priority (lowest first).
     */
    public int priority();

}
