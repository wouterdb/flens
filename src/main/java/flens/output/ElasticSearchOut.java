package flens.output;

import java.io.IOException;
import java.io.UTFDataFormatException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.DateFormatter;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.joda.time.format.DateTimeFormatter;
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import com.mysql.jdbc.Constants;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import flens.core.Matcher;
import flens.core.Record;
import flens.filter.MVELTemplate;
import flens.output.util.AbstractPumpOutput;

public class ElasticSearchOut extends AbstractPumpOutput {

	public class FailListener implements ActionListener<IndexResponse> {

		@Override
		public void onResponse(IndexResponse response) {
			sent++;
		}

		@Override
		public void onFailure(Throwable e) {
			err("elastic search failure", e);
			lost++;
			stop();
			reconnect();
		}

	}

	private boolean closed;
	private String host;
	private int port;
	private Set<String> fields;

	private TransportClient client;
	private CompiledTemplate index;
	private CompiledTemplate type;
	private CompiledTemplate id;

	private DateTimeFormatter df = ISODateTimeFormat.dateTimeNoMillis();
	private ActionListener<IndexResponse> listner = new FailListener();

	public ElasticSearchOut(String name, Matcher matcher, String type,
			String index, String id, List<String> fields, String host, int port) {
		super(name, matcher);
		this.fields = new HashSet<>(fields);

		this.index = TemplateCompiler.compileTemplate(index);
		this.type = TemplateCompiler.compileTemplate(type);

		this.host = host;
		this.port = port;

	}

	@Override
	public void start() {
		closed = false;

		client = new TransportClient();
		client.addTransportAddress(new InetSocketTransportAddress(host, port));
		System.out.println(client.transportAddresses());

		super.start();

	}

	@Override
	public synchronized void stop() {
		super.stop();

		client.close();

		closed = true;
		this.notify();
	}

	public synchronized void join() throws InterruptedException {
		if (!closed)
			this.wait();
	}

	@Override
	public void run() {
		try {
			while (running) {

				try {

					Record r = queue.take();

					Map<String, Object> invalues = r.getValues();
					if (!invalues.containsKey(flens.core.Constants.MESSAGE))
						continue;

					String idx = (String) TemplateRuntime.execute(this.index,
							r.getValues());
					String type = (String) TemplateRuntime.execute(this.type,
							r.getValues());

					Map<String, Object> values = new HashMap<>();

					Set<String> keys = new HashSet<>();

					if (this.fields.isEmpty()) {
						keys.addAll(invalues.keySet());
					} else {
						keys = fields;
					}

					for (String key : keys) {
						values.put("@" + key, invalues.get(key));
					}

					values.put("@message",
							invalues.get(flens.core.Constants.MESSAGE));
					values.put("@timestamp", df.print(r.getTimestamp()));

					IndexRequestBuilder ir;

					if (this.id != null) {
						String id = (String) TemplateRuntime.execute(this.id,
								r.getValues());
						ir = client.prepareIndex(idx, type, id);
					} else {
						ir = client.prepareIndex(idx, type);
					}
					ir.setSource(values);
					ir.execute(listner);
				} catch (RuntimeException e) {
					err("unexpected failure, going into reconnect", e);
					stop();
					reconnect();
				}
			}

		} catch (InterruptedException e) {
			// normal
		}

	}

}
