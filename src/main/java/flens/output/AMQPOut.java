package flens.output;

import java.io.IOException;
import java.io.UTFDataFormatException;
import java.io.UnsupportedEncodingException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import flens.core.Matcher;
import flens.core.Record;
import flens.output.util.AbstractPumpOutput;

public class AMQPOut extends AbstractPumpOutput {

	private String field;
	private ConnectionFactory factory;
	private String exchange;
	private String key;
	private Connection connection;
	private Channel channel;
	private boolean closed;

	public AMQPOut(String name, Matcher matcher, String field, String host,
			int port, String vhost, String user, String pass, String exchange,
			String routingkey) {
		super(name, matcher);
		this.field = field;

		factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setPort(port);
		factory.setUsername(user);
		factory.setPassword(pass);
		if (vhost != null)
			factory.setVirtualHost(vhost);

		this.exchange = exchange;
		this.key = routingkey;

	}

	@Override
	public void start() {
		try {
			closed = false;
			connection = factory.newConnection();

			channel = connection.createChannel();

			try {
				// TODO add config
				channel.exchangeDeclare(exchange, "topic", false);
			} catch (IOException e) {
				e.printStackTrace();
			}

			super.start();

		} catch (IOException e) {
			err("could not connect to amqp", e);
		}

	}

	@Override
	public synchronized void stop() {
		super.stop();
		try {
			channel.close();
			connection.close();
		} catch (IOException e) {
			err("could not close amqp", e);
		}
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

				Record r = queue.take();

				Object raw = r.getValues().get(field);
				byte[] body;

				if (raw instanceof byte[]) {
					body = (byte[]) raw;
				} else if (raw instanceof String) {
					try {
						body = ((String) raw).getBytes("UTF-8");
					} catch (UnsupportedEncodingException e) {
						err("could not use utf-8!", e);
						body = ((String) raw).getBytes();
					}
				} else {
					try {
						body = raw.toString().getBytes("UTF-8");
					} catch (UnsupportedEncodingException e) {
						err("could not use utf-8!", e);
						body = raw.toString().getBytes();
					}
				}

				AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
						.contentEncoding("UTF-8").build();

				channel.basicPublish(exchange, key, props, body);
				sent++;
			}

		} catch (InterruptedException e) {
			// normal
		} catch (IOException e){
			err("AMQP pipe broken",e);
		}
		lost++;

	}

}
