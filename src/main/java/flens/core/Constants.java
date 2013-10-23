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
