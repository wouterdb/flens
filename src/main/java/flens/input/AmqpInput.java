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
        
package flens.input;

import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractInput;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AmqpInput extends AbstractInput implements Consumer {

    private String queue;
    private String exchange;
    private String key;
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private boolean closed;
    private boolean reconnecting = false;
    private int reconnectDelay = 10000;
    private String exchangetype;
    private boolean trycreateexchange;

    /**
     * @param name
     *            name under which this plugin is registered with the engine
     * @param plugin
     *            name of config that loaded this plugin (as registered in
     *            plugins.json)
     * @param tagger
     *            tagger used to mark output records
     * @param host
     *            hostname of the AMQP server
     * @param port
     *            TCP port to connect to
     * @param vhost
     *            vhost on the amqp server
     * @param user
     *            username to use
     * @param pass
     *            password to use
     * @param exchange
     *            exchange to connect to
     * @param queue
     *            queue to use. If null, a unique name is generated
     * @param routingkey
     *            if connected to an exchange, use this as routingkey
     */
    public AmqpInput(String name, String plugin, Tagger tagger, String host, int port, String vhost, String user,
            String pass, String exchange, String exchangetype, boolean trycreateexchange, String queue,
            String routingkey) {
        super(name, plugin, tagger);

        factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(user);
        factory.setPassword(pass);
        if (vhost != null) {
            factory.setVirtualHost(vhost);
        }

        this.queue = queue;
        this.exchange = exchange;
        this.key = routingkey;
        this.exchangetype = exchangetype;
        this.trycreateexchange = trycreateexchange;
    }

    @Override
    public synchronized void start() {
        closed = false;
        reconnecting = false;
        try {
            connection = factory.newConnection();

            channel = connection.createChannel();

            String queue;

            if (this.queue == null) {
                queue = channel.queueDeclare().getQueue();
            } else {
                queue = this.queue;
                if (channel.queueDeclarePassive(queue) == null) {
                    // TODO add config
                    channel.queueDeclare(queue, false, false, true, null);
                }
            }

            if (exchange != null) {
                if (trycreateexchange) {
                    try {
                        // TODO add config
                        channel.exchangeDeclare(exchange, exchangetype, false);
                    } catch (IOException e) {
                        warn("could not create exchange", e);
                    }
                }
                if (key != null) {
                    channel.queueBind(queue, exchange, key);
                }

            }

            channel.basicConsume(queue, true, this);

        } catch (IOException e) {
            err("could not connect to amqp", e);
            reconnect();
        }

    }

    @Override
    public synchronized void stop() {
        if (closed) {
            return;
        }
        try {
            channel.close();
            connection.close();
        } catch (IOException e) {
            err("could not close amqp", e);
        }

        closed = true;
        this.notify();
    }

    @Override
    public synchronized void join() throws InterruptedException {
        while (!closed) {
            this.wait();
        }
    }

    @Override
    public void handleConsumeOk(String consumerTag) {
    }

    @Override
    public void handleCancelOk(String consumerTag) {

    }

    @Override
    public void handleCancel(String consumerTag) throws IOException {
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
        throws IOException {
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("amqp-tag", consumerTag);
        fields.put("body", body);
        fields.put("mine-type", properties.getContentEncoding());

        Date time = properties.getTimestamp();
        long timel = time == null ? System.currentTimeMillis() : time.getTime();

        Record out = Record.forTransport(timel, fields);
        dispatch(out);

    }

    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
        stop();
        reconnect();
    }

    protected synchronized void reconnect() {
        if (closed) {
            return;
        }
        // re-entrant
        if (reconnecting) {
            return;
        }
        reconnecting = true;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

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
    }

}
