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
        
package flens.output;

import flens.core.Matcher;
import flens.core.Record;
import flens.output.util.AbstractPumpOutput;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class AmqpOut extends AbstractPumpOutput {

    private String field;
    private ConnectionFactory factory;
    private String exchange;
    private String key;
    private Connection connection;
    private Channel channel;
    private boolean closed;
    protected String host;
    protected int port;
    protected String user;
    protected String pass;
    protected String vhost;

    /**
     * @param name
     *            name under which this plugin is registered with the engine
     * @param plugin
     *            name of config that loaded this plugin (as registered in
     *            plugins.json)
     * @param matcher
     *            matcher this output should used to select records
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
     * @param routingkey
     *            if connected to an exchange, use this as routingkey
     */
    public AmqpOut(String name, String plugin, Matcher matcher, String field, String host, int port, String vhost,
            String user, String pass, String exchange, String routingkey) {
        super(name, plugin, matcher);
        this.field = field;

        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.vhost = vhost;

        factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(user);
        factory.setPassword(pass);
        if (vhost != null) {
            factory.setVirtualHost(vhost);
        }

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
            reconnect();
        }

    }

    @Override
    public synchronized void stop() {
        super.stop();
        try {
            channel.close();
            connection.close();
        } catch (Exception e) {
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
    public void run() {
        try {
            while (running) {

                Record record = queue.take();

                Object raw = record.getValues().get(field);
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

                AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().contentEncoding("UTF-8").build();

                channel.basicPublish(exchange, key, props, body);
                sent++;
            }

        } catch (InterruptedException e) {
            // normal for stop
            stop();
        } catch (IOException e) {
            err("AMQP pipe broken", e);
            stop();
            lost++;
            reconnect();
        }

    }

}
