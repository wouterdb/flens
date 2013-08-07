package flens.input;

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

import com.sun.tools.classfile.Dependency.Finder;

import flens.core.Constants;
import flens.core.Record;
import flens.core.Tagger;
import flens.input.jmx.JMXUtils;
import flens.input.jmx.JVM;
import flens.input.jmx.VMSelectionException;
import flens.input.util.AbstractPeriodicInput;

public class JMXInput extends AbstractPeriodicInput {

	public class JMXInputWorker extends TimerTask {

		private int cycle = 0;
		private Set<JVM> jvms = new HashSet<>();

		@Override
		public void run() {
			if (cycle == 0)
				findJVMs();
			cycle = (cycle + 1) % findJvmMultiplier;
			pumpMetrics();
		}

		private void pumpMetrics() {
			for (Iterator jmvit = jvms.iterator(); jmvit.hasNext();) {
				JVM jvm = (JVM) jmvit.next();
				try {
					Set<ObjectName> beans = collectBeans(jvm);
					dispatch(jvm.getConnection(), beans);
				} catch (IOException e) {
					warn("jvm lost", e);
					try {
						jvm.disconnect();
					} catch (IOException e1) {
						warn("jvm connection close failed", e);
					}
					jmvit.remove();
				}
			}
			
		}

		private void dispatch(MBeanServerConnection con, Set<ObjectName> beans)
				throws IOException {
			for (ObjectName bean : beans) {
				dispatch(con, bean);
			}

		}

		private void dispatch(MBeanServerConnection con, ObjectName name)
				throws IOException {
			try {
				Record r = Record.createWithValues(	new HashMap<String, Object>(name.getKeyPropertyList()));
				MBeanInfo mbi = con.getMBeanInfo(name);

				for (MBeanAttributeInfo mbai : mbi.getAttributes()) {
					if (!mbai.isReadable())
						continue;
					
					Record r2 = r.doClone();
					String metric = mbai.getName();
					if(metric.equals("Value")){
						String type = (String) r2.getValues().remove("type");
						
						if(type!=null)
							metric = type;
					}
					r2.setValue(Constants.METRIC,metric);
					Object val = con.getAttribute(name, mbai.getName()); 
					/*if(val instanceof CompositeData)
						dispatch(r2,(CompositeData)val);
					else*/
						r2.setValue(Constants.VALUE,val);
					JMXInput.this.dispatch(r2);
				}

			} catch (InstanceNotFoundException | IntrospectionException
					| ReflectionException | AttributeNotFoundException
					| MBeanException e) {
				err("bean went haywire", e);
			}

		}

		private void dispatch(Record parent, CompositeData val) {
			CompositeType ct = val.getCompositeType();
			for(String type:ct.keySet()){
				Record cur = parent.doClone();
				cur.setValue(Constants.TYPE, type);
				cur.setValue(Constants.VALUE, val.get(type));
				JMXInput.this.dispatch(cur);
			}
			
		}

		private Set<ObjectName> collectBeans(JVM jvm) throws IOException {
			Set<ObjectName> out = new HashSet<>();
			for (String domain : domains)
				try {
					if(domain.contains(":"))
						out.addAll(jvm.getConnection().queryNames(
								new ObjectName(domain), null));
					else
						out.addAll(jvm.getConnection().queryNames(
							new ObjectName(domain + ":*"), null));
				} catch (MalformedObjectNameException e) {
					err("bad domain selector", e);
				}
			return out;
		}

		private void findJVMs() {
			try {
				Set<JVM> jvms = JMXUtils.getJVMs(JVMselector);
				jvms.removeAll(this.jvms);
				this.jvms.addAll(jvms);
				for (JVM jvm : jvms) {
					info("found jvm: " + jvm);
				}
			} catch (VMSelectionException e) {
				err("problem finding JVM's", e);
			}
		}

		public void stop() {
			for (JVM vm : jvms) {
				try {
					vm.disconnect();
				} catch (IOException e) {
					err("exception trying to disconnect jmx", e);
				}
			}
		}

	}

	private int findJvmMultiplier;
	private String JVMselector;
	private JMXInputWorker worker;
	private String[] domains;

	public JMXInput(String name, Tagger tagger, String jvmSelector,
			List<String> domains, int interval, int findJVMMultiplier) {
		super(name, tagger, interval);
		this.findJvmMultiplier = findJVMMultiplier;
		this.JVMselector = jvmSelector;
		this.domains = domains.toArray(new String[domains.size()]);
	}

	@Override
	protected TimerTask getWorker() {
		worker = new JMXInputWorker();
		return worker;
	}

	@Override
	public void stop() {
		super.stop();
		worker.stop();
	}

}
