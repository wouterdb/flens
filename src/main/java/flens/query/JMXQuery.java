package flens.query;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.sun.tools.classfile.Dependency.Finder;

import flens.core.Constants;
import flens.core.Query;
import flens.core.QueryHandler;
import flens.core.Record;
import flens.core.Tagger;
import flens.core.util.AbstractPlugin;
import flens.input.jmx.JMXUtils;
import flens.input.jmx.JVM;
import flens.input.jmx.VMSelectionException;
import flens.input.util.AbstractPeriodicInput;

public class JMXQuery extends AbstractPlugin implements QueryHandler {

	private MBeanServerConnection connection;
	private JMXConnector con;

	private String host;
	private int port;

	private boolean running = true;

	private synchronized void connect() throws IOException {
		if (!running)
			throw new IllegalStateException("connecting while shutting down");
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"
				+ host + ":" + port + "/jmxrmi");
		con = JMXConnectorFactory.connect(url, null);
		connection = con.getMBeanServerConnection();
	}

	private String name;

	public JMXQuery(String name, String host, int port) {
		this.host = host;
		this.port = port;
		this.name = name;
	}

	@Override
	public synchronized void stop() {
		running = false;
		try {
			if (con != null) {

				con.close();

				con = null;
				connection = null;
			}
		} catch (IOException e) {
			err("could not close", e);
		}
	}

	@Override
	public synchronized void start() {
		// lazy is fine!
		running = true;
	}

	@Override
	public boolean canHandle(Query q) {
		if (!q.getQuery().startsWith("jmx:"))
			return false;
		if (q.getQuery().startsWith("jmx:*://"))
			return true;
		if (q.getQuery().startsWith("jmx:"+getName()+"://"))
			return true;
		return false;
	}

	@Override
	//fixme: make non-sync, but handle connect well
	public synchronized void handle(Query q) {
		if(con == null)
			connect();
		String url = new

	}

	@Override
	public void join() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
