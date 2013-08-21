package flens.query;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
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
		if (q.getQuery().startsWith("jmx:" + getName() + "://"))
			return true;
		return false;
	}

	@Override
	// fixme: make non-sync, but handle connect well
	public synchronized void handle(Query q) {
		try {
			if (con == null)
				connect();
			String url = q.getQuery();
			String[] parts = url.split("/");
			String method = parts[2];
			String[] rest = ArrayUtils.subarray(parts, 3, Integer.MAX_VALUE);

			switch(method){
				case "list-names": list(q); break;
				case "list": listLong(q);break;
				case "details": details(q,rest);break;
				case "call": call(q,rest);break;
				case "get": get(q,rest);break;
			}
			
			 
		} catch (Exception e) {
			q.fail(500, e.getMessage());
		}

	}

	private void call(Query q, String[] rest) throws MalformedObjectNameException, NullPointerException, IOException, InstanceNotFoundException, MBeanException, ReflectionException {
		String name = rest[0];
		String oppname = rest[1];
		
		Set<ObjectName> names = new TreeSet<ObjectName>(connection.queryNames(ObjectName.getInstance(name), null));
		
		Map<String,Object> out = new HashMap<>();
		
		for (ObjectName objectName : names) {
			List<Object> o = (List<Object>) q.getPayload().get("params");
			List<String> sig = (List<String>) q.getPayload().get("sig");
			out.put(objectName.getCanonicalName(), connection.invoke(objectName, oppname,o.toArray(),sig.toArray(new String[sig.size()])));
		}
		
		Gson gson = new Gson();
		q.respond(gson.toJson(out));
		
	}

	private void listLong(Query q) throws IOException {
		Set<ObjectName> names = new TreeSet<ObjectName>(connection.queryNames(null, null));
		List<String> out = new LinkedList<>();
		for (ObjectName objectName : names) {
			out.add(objectName.getCanonicalName());
		}
		Gson gson = new Gson();
		q.respond(gson.toJson(out));
	}

	private void details(Query q, String[] rest) throws MalformedObjectNameException, NullPointerException, IOException, InstanceNotFoundException, IntrospectionException, ReflectionException {
		String name = rest[0];
		
		Set<ObjectName> names = new TreeSet<ObjectName>(connection.queryNames(ObjectName.getInstance(name), null));
		
		Map<String,Object> out = new HashMap<>();
		
		for (ObjectName objectName : names) {
			out.put(objectName.getCanonicalName(), getDetails(objectName));
		}
		
		Gson gson = new Gson();
		q.respond(gson.toJson(out));
		
		
	}
	
	private void get(Query q, String[] rest) throws MalformedObjectNameException, NullPointerException, IOException, InstanceNotFoundException, IntrospectionException, ReflectionException, AttributeNotFoundException, MBeanException {
		String name = rest[0];
		String att = rest[1];
		
		Set<ObjectName> names = new TreeSet<ObjectName>(connection.queryNames(ObjectName.getInstance(name), null));
		
		Map<String,Object> out = new HashMap<>();
		
		for (ObjectName objectName : names) {
			out.put(objectName.getCanonicalName(), connection.getAttribute(objectName, att));
		}
		
		Gson gson = new Gson();
		q.respond(gson.toJson(out));
		
		
	}

	private Map<String,Object> getDetails(ObjectName objectName) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
		Map<String,Object> out = new HashMap<>();
		MBeanInfo info = connection.getMBeanInfo(objectName);
		out.put("description", info.getDescription());
		out.put("attributes", getAttributes(info));
		out.put("operations", getOpperations(info));
		out.put("notifications", getNotifications(info));
		return out;
		
	}

	private Map<String,Object> getNotifications(MBeanInfo info) {
		Map<String,Object> outc = new HashMap<>();
		MBeanNotificationInfo[] atts = info.getNotifications();
		for (MBeanNotificationInfo att : atts) {
			Map<String,Object> out = new HashMap<>();
			outc.put(att.getName(), out);
			out.put("description", att.getDescription());
			out.put("type", att.getNotifTypes());
		}
		return outc;
	}

	private Map<String,Object> getAttributes(MBeanInfo info) {
		Map<String,Object> outc = new HashMap<>();
		MBeanAttributeInfo[] atts = info.getAttributes();
		for (MBeanAttributeInfo att : atts) {
			Map<String,Object> out = new HashMap<>();
			outc.put(att.getName(), out);
			out.put("description", att.getDescription());
			out.put("type", att.getType());
		}
		return outc;
	}

	private Map<String,Object> getOpperations(MBeanInfo info) {
		Map<String,Object> outc = new HashMap<>();
		MBeanOperationInfo[] atts = info.getOperations();
		for (MBeanOperationInfo att : atts) {
			Map<String,Object> out = new HashMap<>();
			outc.put(att.getName(), out);
			out.put("description", att.getDescription());
			out.put("return-type", att.getReturnType());
			out.put("signature",getSig(att.getSignature()));
					
		}
		return outc;
	}

	
	private List getSig(MBeanParameterInfo[] signature) {
		List outc = new LinkedList<>();
		
		for (MBeanParameterInfo att : signature) {
			Map<String,Object> out = new HashMap<>();
			outc.add(out);
			out.put("name", att.getName());
			out.put("description", att.getDescription());
			out.put("type", att.getType());	
		}
		return outc;
	}

	private void list(Query q) throws IOException {
		q.respond(StringUtils.join(connection.getDomains(), ","));
	}

	@Override
	public void join() {

	}

	@Override
	public String getName() {
		return name;
	}

}
