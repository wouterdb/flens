package flens.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;

public class Record {

	private String source;
	private String type;
	private Set<String> tags;
	private long timestamp;
	private Map<String, Object> values;

	public Record(String type, long timestamp) {
		super();
		this.type = type;
		this.timestamp = timestamp;
		this.source = Util.hostName();
		tags = new HashSet<String>();
		values = new HashMap<String, Object>();
	}

	public Record(String type, String message) {
		this(type, System.currentTimeMillis());

		values.put(Constants.L_MESSAGE, message);
	}

	public Record(String type) {
		super();
		this.type = type;
		this.source = Util.hostName();
		this.timestamp = System.currentTimeMillis();
		tags = new HashSet<String>();
		values = new HashMap<String, Object>();
	}

	public Record(String type, long timestamp, String host,
			Map<String, Object> values) {
		super();
		this.type = type;
		this.timestamp = timestamp;
		this.source = host;
		this.values = values;
		
	}
	
	public Record(String type, long timestamp, String host,
			Map<String, Object> values,Set<String> tags) {
		super();
		this.type = type;
		this.timestamp = timestamp;
		this.source = host;
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
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public Map<String, Object> getValues() {
		return values;
	}

	public void setValues(Map<String, Object> values) {
		this.values = values;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public String toString() {
		return toLine();
	}

	public String toLine() {
		return "Record [type=" + type + ", timestamp=" + timestamp
				+ ", source=" + source + ", tags=" + tags + ", values="
				+ values + "]";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new Record(type,timestamp,source,new HashMap<String, Object>(values),new HashSet<String>(tags)) ;
	}
	
	public Record cloneNoValues()  {
		return new Record(type,timestamp,source,null,new HashSet<String>(tags)) ;
	}

}
