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
package flens.input;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractActiveInput;
import flens.input.util.AbstractInput;

public class AMQPInput extends AbstractInput implements Consumer {

	private String queue;
	private String exchange;
	private String key;
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private boolean closed;
	private boolean reconnecting = false;
	private int reconnectDelay = 10000;

	public AMQPInput(String name, Tagger tagger, String host, int port,
			String vhost, String user, String pass, String exchange,
			String queue, String routingkey) {
		super(name, tagger);

		factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setPort(port);
		factory.setUsername(user);
		factory.setPassword(pass);
		if (vhost != null)
			factory.setVirtualHost(vhost);

		this.queue = queue;
		this.exchange = exchange;
		this.key = routingkey;
	}

	@Override
	public synchronized void start() {
		closed = false;
		reconnecting=false;
		try {
			connection = factory.newConnection();

			channel = connection.createChannel();

			String queue;

			if (this.queue == null)
				queue = channel.queueDeclare().getQueue();
			else {
				queue = this.queue;
				if (channel.queueDeclarePassive(queue) == null) {
					// TODO add config
					channel.queueDeclare(queue, false, false, true, null);
				}
			}

			if (exchange != null) {
				try{
					// TODO add config
					channel.exchangeDeclare(exchange, "topic", false);
				}catch(IOException e){
					e.printStackTrace();
				}
				if (key != null) {
					channel.queueBind(queue, exchange, key);
				}

			}

			channel.basicConsume(queue, true,this);

		} catch (IOException e) {
			err("could not connect to amqp", e);
			reconnect();
		}

	}

	@Override
	public synchronized void stop() {
		try {
			channel.close();
			connection.close();
		} catch (IOException e) {
			err("could not close amqp",e);
		}
		
		closed = true;
		this.notify();
	}

	@Override
	public synchronized void join() throws InterruptedException {
		if(!closed)
			this.wait();
	}

	@Override
	public void handleConsumeOk(String consumerTag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCancelOk(String consumerTag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCancel(String consumerTag) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope,
			BasicProperties properties, byte[] body) throws IOException {
		Map<String,Object> fields = new HashMap<String, Object>();
		fields.put("tag", consumerTag);
		fields.put("body", body);
		fields.put("mine-type", properties.getContentEncoding());
		
		Date time = properties.getTimestamp();
		long timel = time==null?System.currentTimeMillis():time.getTime();
		
		
		Record r = Record.createWithTimeAndValues(timel,fields);
		dispatch(r);
			
	}

	@Override
	public void handleShutdownSignal(String consumerTag,
			ShutdownSignalException sig) {
		stop();
		reconnect();
	}

	protected synchronized void reconnect() {
		// re-entrant
		if (reconnecting)
			return;
		reconnecting = true;
		Timer t = new Timer();
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					start();
				} catch (Exception e) {
					err("reconnect failed", e);
					reconnect();
				}
			}
		}, reconnectDelay);

	}
	
	@Override
	public void handleRecoverOk(String consumerTag) {
		// TODO Auto-generated method stub

	}

}
