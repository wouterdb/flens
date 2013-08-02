package flens.config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import flens.core.Config.Option;
import flens.core.util.AbstractConfig;
import flens.input.OpenTsdbInput;

public class ElasticSearchOutput extends AbstractConfig {

	@Override
	protected void construct() {
		String type = get("type-label","syslog");
		String index = get("index","logstash-@{(new java.text.SimpleDateFormat('yyyy.MM.dd')).format(new java.util.Date(Long.parseLong(time)))}");
		String id = get("id",null);
		String host = get("host", "localhost");
		int port = getInt("port",9300);
		List<String> fields = getArray("fields", Collections.EMPTY_LIST);
		Map<String,String> config = (Map<String, String>) tree.remove("transport");
		engine.addOutput(new flens.output.ElasticSearchOut(name, matcher, type, index, id, fields,host,port));
	}

	
	@Override
	protected boolean isIn() {
		return false;
	}

	@Override
	protected boolean isOut() {
		return true;
	}
	
	@Override
	public List<Option> getOptions() {
		List<Option>  out = new LinkedList(super.getOptions());
	
		out.add(new Option("host", "String", "localhost", "host to connect to"));
		out.add(new Option("port", "int", "4369", "port to connect to"));
		return out;
	}


	@Override
	public String getDescription() {
		return "Listen on TCP socket for opentsdb messages";
	}

}
