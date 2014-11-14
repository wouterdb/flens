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

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import flens.typing.MetricType;

//time in milis
public class Record {

	private String type;
	private Set<String> tags;
	private Map<String, Object> values;

	@Deprecated
	public Record(String message) {
		this.tags = new HashSet<String>();
		this.values = new HashMap<String, Object>();
		values.put(Constants.TIME, System.currentTimeMillis());
		values.put(Constants.SOURCE, Util.hostName());
		values.put(Constants.L_MESSAGE, message);
	}

	@Deprecated
	public Record(String metric, Object value) {
		this.tags = new HashSet<String>();
		this.values = new HashMap<String, Object>();
		values.put(Constants.TIME, System.currentTimeMillis());
		values.put(Constants.SOURCE, Util.hostName());
		values.put(Constants.METRIC, metric);
		values.put(Constants.VALUE, value);
	}

	@Deprecated
	public static Record createWithTimeHostAndValues(long timestamp,
			String host, Map<String, Object> values) {

		values.put(Constants.TIME, timestamp);
		values.put(Constants.SOURCE, host);

		return new Record(null, values, new HashSet<String>());
	}
	
	@Deprecated
	public static Record createWithTimeAndValues(long timestamp,
			Map<String, Object> values) {
		values.put(Constants.TIME, timestamp);
		values.put(Constants.SOURCE, Util.hostName());
		return new Record(null, values, new HashSet<String>());
	}

	
	/*************************
	 * BLOB
	 *************************/

	public static Record forBlobWithHost(String host,
			byte[] body) {

		Map<String, Object> values = new HashMap<String, Object>();
		values.put(Constants.TIME, System.currentTimeMillis());
		values.put(Constants.SOURCE, host);
		values.put(Constants.BODY, body);

		return new Record(null, values, new HashSet<String>());
	}
	
	public static Record forBlob(String metric,
			byte[] body) {

		Map<String, Object> values = new HashMap<String, Object>();
		values.put(Constants.TIME, System.currentTimeMillis());
		values.put(Constants.SOURCE, Util.hostName());
		values.put(Constants.BODY, body);
		values.put(Constants.METRIC, metric);

		return new Record(null, values, new HashSet<String>());
	}
	
	@Deprecated
	public static Record createWithValue(String metric, byte[] payload) {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put(Constants.TIME, System.currentTimeMillis());
		values.put(Constants.SOURCE, Util.hostName());
		values.put(Constants.BODY, payload);
		values.put(Constants.METRIC, metric);
		return new Record(null, values, new HashSet<String>());
	}
	
	/*************************
	 * MSG - LOG 
	 *************************/

	public static Record createWithMessage(String msg) {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put(Constants.TIME, System.currentTimeMillis());
		values.put(Constants.SOURCE, Util.hostName());
		values.put(Constants.MESSAGE, msg);
		return new Record(null, values, new HashSet<String>());
	}

	public static Record createWithHostAndMessage(String host, String msg) {

		Map<String, Object> values = new HashMap<String, Object>();
		values.put(Constants.TIME, System.currentTimeMillis());
		values.put(Constants.SOURCE, host);
		values.put(Constants.MESSAGE, msg);

		return new Record(null, values, new HashSet<String>());
	}
	
	//FIXME: text metric Vs Log, smart or not?
	public static Record forTextMetric(String metric, String message) {
		Map<String, Object> values = new HashMap<String, Object>();
		Set<String> tags = new HashSet<String>();
		values.put(Constants.TIME, System.currentTimeMillis());
		values.put(Constants.SOURCE, Util.hostName());
		values.put(Constants.MESSAGE, message);
		values.put(Constants.METRIC, metric);
		return new Record(null, values, tags);
	}
	
	
	public static Record forLog(String message) {
		return createWithMessage(message);
	}
	
	public static Record forLog(String file, String message) {
		Map<String, Object> values = new HashMap<String, Object>();
		Set<String> tags = new HashSet<String>();
		values.put(Constants.TIME, System.currentTimeMillis());
		values.put(Constants.SOURCE, Util.hostName());
		values.put(Constants.MESSAGE, message);
		values.put(Constants.LOG_FILE, file);
		return new Record(null, values, tags);
	}

	/*************************
	 *  METRIC
	 *************************/
	
	@Deprecated
	public static Record createWithTimeAndValue(long timestamp, String metric,
			long number) {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put(Constants.TIME, timestamp);
		values.put(Constants.SOURCE, Util.hostName());
		values.put(Constants.METRIC, metric);
		values.put(Constants.VALUE, number);
		return new Record(null, values, new HashSet<String>());
	}
	
	public static Record createWithTimeAndValue(long timestamp, String metric,
			long number, String unit) {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put(Constants.TIME, timestamp);
		values.put(Constants.SOURCE, Util.hostName());
		values.put(Constants.METRIC, metric);
		values.put(Constants.VALUE, number);
		values.put(Constants.UNIT, unit);
		return new Record(null, values, new HashSet<String>());
	}
	
	@Deprecated
	public static Record createWithValue(String metric, long delta) {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put(Constants.TIME, System.currentTimeMillis());
		values.put(Constants.SOURCE, Util.hostName());
		values.put(Constants.VALUE, delta);
		values.put(Constants.METRIC, metric);
		return new Record(null, values, new HashSet<String>());
	}
	
	public static Record forMetric(String metric,
			long number, String unit) {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put(Constants.TIME, System.currentTimeMillis());
		values.put(Constants.SOURCE, Util.hostName());
		values.put(Constants.METRIC, metric);
		values.put(Constants.VALUE, number);
		values.put(Constants.UNIT, unit);
		return new Record(null, values, new HashSet<String>());
	}

	@Deprecated
	public static Record createWithTypeTimeAndValue(long timestamp,
			String metric, String type, long value) {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put(Constants.TIME, timestamp);
		values.put(Constants.SOURCE, Util.hostName());
		values.put(Constants.METRIC, metric);
		values.put(Constants.TYPE, type);
		values.put(Constants.VALUE, value);
		return new Record(null, values, new HashSet<String>());
	}
	
	public static Record createWithTypeTimeAndValue(long timestamp,
			String metric, String type, long value, String unit) {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put(Constants.TIME, timestamp);
		values.put(Constants.SOURCE, Util.hostName());
		values.put(Constants.METRIC, metric);
		values.put(Constants.TYPE, type);
		values.put(Constants.VALUE, value);
		values.put(Constants.UNIT, unit);
		return new Record(null, values, new HashSet<String>());
	}

	
	public static Record createWithValues(Map<String, Object> values) {

		for (Map.Entry<String, Object> entries : new HashSet<>(
				values.entrySet())) {
			if (entries.getValue() == null)
				values.remove(entries.getKey());
		}

		if (!values.containsKey(Constants.TIME))
			values.put(Constants.TIME, System.currentTimeMillis());
		if (!values.containsKey(Constants.SOURCE))
			values.put(Constants.SOURCE, Util.hostName());

		return new Record(null, values, new HashSet<String>());
	}

	@Deprecated
	public static Record createWithValues(String metric,
			Map<String, Object> values) {
		if (!values.containsKey(Constants.TIME))
			values.put(Constants.TIME, System.currentTimeMillis());
		if (!values.containsKey(Constants.SOURCE))
			values.put(Constants.SOURCE, Util.hostName());
		values.put(Constants.METRIC, metric);
		return new Record(null, values, new HashSet<String>());
	}

	

	

	@Deprecated
	public static Record createWithValue(String metric, String msg) {
		return createWithMetricAndMsg(metric, msg);
	}

	@Deprecated
	public static Record createWithMetricAndMsg(String metric, String msg) {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put(Constants.TIME, System.currentTimeMillis());
		values.put(Constants.SOURCE, Util.hostName());
		values.put(Constants.METRIC, metric);
		values.put(Constants.MESSAGE, msg);
		return new Record(null, values, new HashSet<String>());
	}

	

	public static Record pack(List<Record> collector) {
		Map<String, Object> values = new HashMap<String, Object>();
		Set<String> tags = new HashSet<String>();
		values.put(Constants.TIME, System.currentTimeMillis());
		values.put(Constants.SOURCE, Util.hostName());
		values.put(Constants.SUBRECORDS, collector);
		return new Record(null, values, tags);
	}

	public Record() {
		super();
		tags = new HashSet<String>();
		values = new HashMap<String, Object>();
		values.put(Constants.TIME, System.currentTimeMillis());
		values.put(Constants.SOURCE, Util.hostName());
	}

	/*
	 * public Record(String type, long timestamp, String host, Map<String,
	 * Object> values,Set<String> tags) { super(); this.type = type; this.values
	 * = values; this.tags = tags; values.put(Constants.TIME, timestamp);
	 * values.put(Constants.SOURCE, host); }
	 */
	protected Record(String type, Map<String, Object> values, Set<String> tags) {
		this.type = type;
		this.values = values;
		this.tags = tags;
	}

	public Record(Number value, MetricType intype) {
		tags = new HashSet<String>();
		values = new HashMap<String, Object>();
		values.put(Constants.TIME, System.currentTimeMillis());
		values.put(Constants.SOURCE, Util.hostName());
		values.put(Constants.METRIC, intype.getName());
		values.put(Constants.VALUE, value);
		addMeta(intype);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public long getTimestamp() {
		Object o = values.get(Constants.TIME);
		if (o instanceof Number)
			return ((Number) o).longValue();
		if (o instanceof Date)
			return ((Date) o).getTime();
		if (o instanceof String)
			return Long.parseLong((String) o);
		throw new IllegalStateException("timestamp of unexpected form:" + o
				+ o.getClass());
	}

	/**
	 * @param timestamp
	 * 
	 *            milis since epoch
	 */
	public void setTimestamp(long timestamp) {
		values.put(Constants.TIME, timestamp);
	}

	public Map<String, Object> getValues() {
		return values;
	}

	public void setValues(Map<String, Object> values) {
		this.values = values;
	}

	public String getSource() {
		return (String) values.get(Constants.SOURCE);
	}

	public void setSource(String source) {
		values.put(Constants.SOURCE, source);
	}

	@Override
	public String toString() {
		return toLine();
	}

	public String toLine() {
		return "Record [type=" + type + " tags=" + tags + ", values=" + values
				+ "]";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new Record(type, new HashMap<String, Object>(values),
				new HashSet<String>(tags));
	}

	public Record doClone() {
		return new Record(type, new HashMap<String, Object>(values),
				new HashSet<String>(tags));
	}

	public Record cloneNoValues() {
		return new Record(type, null, new HashSet<String>(tags));
	}

	public void setValue(String name, Object value) {
		if (value == null)
			values.remove(name);
		else
			values.put(name, value);

	}

	
	public byte[] getBytes(String field) {
		Object raw = values.get(field);
		
		byte[] body;

		if (raw instanceof byte[]) {
			body = (byte[]) raw;
		} else if (raw instanceof String) {
			try {
				body = ((String) raw).getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new Error("could not use utf-8!", e);
			}
		} else {
			try {
				body = raw.toString().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new Error("could not use utf-8!", e);
			}
		}

		return body;
	}

	public void addMeta(MetricType metricType) {
		values.put("unit",metricType.getUnit());
		values.put("resourcetype",metricType.getResource());
		values.put("form",metricType.getForm().toString());
		values.put("range",metricType.getRange());
	}

	public static Record createFromTypeAndInstance(Number value, MetricType intype, String instance) {
		Record r = new Record(value, intype);
		r.values.put(Constants.INSTANCE, instance);
		return r;
	}
	

}
