package flens.core;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;


//time in milis
public class Record {

	private String type;
	private Set<String> tags;
	private Map<String, Object> values;


	public Record(String type, String message) {
		
		this.type = type;
		this.tags = new HashSet<String>();
		this.values = new HashMap<String, Object>();
		values.put(Constants.TIME, System.currentTimeMillis());
		values.put(Constants.SOURCE,  Util.hostName());
		values.put(Constants.L_MESSAGE, message);
	}

	public Record(String type) {
		super();
		this.type = type;
		tags = new HashSet<String>();
		values = new HashMap<String, Object>();
		values.put(Constants.TIME, System.currentTimeMillis());
		values.put(Constants.SOURCE,  Util.hostName());
	}

	public Record(String type, long timestamp, String host,
			Map<String, Object> values) {
		super();
		this.type = type;
		this.values = values;
		this.tags = new HashSet<String>();
		values.put(Constants.TIME, timestamp);
		values.put(Constants.SOURCE,  host);
	}
	
	public Record(String type, long timestamp, String host,
			Map<String, Object> values,Set<String> tags) {
		super();
		this.type = type;
		this.values = values;
		this.tags = tags;
		values.put(Constants.TIME, timestamp);
		values.put(Constants.SOURCE,  host);
	}

	public Record(String type, HashMap<String, Object> values,
			HashSet<String> tags) {
		this.type = type;
		this.values = values;
		this.tags = tags;
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
		if(o instanceof Number)
			return ((Number)o).longValue();
		if(o instanceof Date)
			return ((Date)o).getTime();
		throw new IllegalStateException("timestamp of unexpected form:" + o+o.getClass());
	}

	/**
	 * @param timestamp
	 * 
	 * milis since epoch
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
		return  (String) values.get(Constants.SOURCE);
	}

	public void setSource(String source) {
		values.put(Constants.SOURCE, source);
	}

	@Override
	public String toString() {
		return toLine();
	}

	public String toLine() {
		return "Record [type=" + type + " tags=" + tags + ", values="
				+ values + "]";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new Record(type,new HashMap<String, Object>(values),new HashSet<String>(tags)) ;
	}
	
	public Record doClone() {
		return new Record(type,new HashMap<String, Object>(values),new HashSet<String>(tags)) ;
	}
	
	public Record cloneNoValues()  {
		return new Record(type,null,new HashSet<String>(tags)) ;
	}

	public void setValue(String name, Object value) {
		if(value==null)
			values.remove(name);
		else
			values.put(name, value);
		
	}

}
