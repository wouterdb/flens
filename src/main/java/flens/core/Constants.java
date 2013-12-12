/**
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

/**
 * @author wouterdb
 *
 */
public class Constants {
	
	//obligatory fields
	/**
	 * obligatory field
	 * 
	 * time in ms since epoch (unix timestamp) 
	 */
	public static final String TIME = "time";
	/**
	 * obligatory field
	 * 
	 * hostname of machine to which the record appertains
	 */
	public static final String SOURCE = "source";
	/**
	 * obligatory field for metrics
	 * 
	 * primary name of the metric
	 */
	public static final String METRIC = "metric";
	
	/**
	 * obligatory field for logs
	 * 
	 * primary name of the metric
	 */
	public static final String LOG_FILE = "file";
	
	//primary value fields
	
	/**
	 * primary value field
	 * 
	 * text message
	 */
	public static final String L_MESSAGE = "message";
	public static final String MESSAGE = "message";
	
	/**
	 * primary value field
	 * 
	 * binary message
	 */
	public static final String BINARY_MESSAGE = "body";
	public static final String BODY = "body";
	
	/**
	 * primary value field
	 * 
	 * numerical value message
	 */
	public static final String VALUE = "value";
	
	
	
	
	
	public static final Matcher ALLMATCHER = new AllMatcher();
	
	
	
	
	public static final String INTERVAL = "interval";
	
	
	public static final String PLUGIN = "plugin";
	public static final String PLUGIN_INSTANCE = "plugin_instance";
	public static final String TYPE = "type";
	public static final String TYPE_INSTANCE = "type_instance";
	
	public static final String SEVERITY = "severity";
	public static final String VALUES = "values";
	public static final String TAGS = "tags";
	public static final String UNIT = "unit";
	public static final String TARGET = "target";
	
	

}
