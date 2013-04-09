// based on tcollector/stumbleupon/monitoring/jmx.java
// https://github.com/OpenTSDB/tcollector/blob/master/stumbleupon/monitoring/jmx.java
//
// This file is part of OpenTSDB.
// Copyright (C) 2010 StumbleUpon, Inc.
//
// This program is free software: you can redistribute it and/or modify it
// under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or (at your
// option) any later version. This program is distributed in the hope that it
// will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
// of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
// General Public License for more details. You should have received a copy
// of the GNU Lesser General Public License along with this program. If not,
// see <http://www.gnu.org/licenses/>.

package flens.input.jmx;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.TabularData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

//Sun specific
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

//Sun private
import sun.management.ConnectorAddressLink;
import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

public class JMXUtils {

	public static final String LOCAL_CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";
	private static final String MAGIC_KEY = "this.is.jmx.magic";
	private static final String MAGIC_VALUE = "exclude this vm from JMX inspection please";

	private static Pattern compile_re(final String re) {

		return Pattern.compile(re);

	}

	
	
	
	
	public static void markForExclusion() {
		System.setProperty(MAGIC_KEY, MAGIC_VALUE);
	}

	public static Set<flens.input.jmx.JVM> selectJVM(final String selector,
			final HashMap<Integer, JVM> vms) throws VMSelectionException {

		try {
			final int pid = Integer.parseInt(selector);
			if (pid < 2) {
				throw new IllegalArgumentException("Invalid PID: " + pid);
			}
			final JVM jvm = vms.get(pid);
			if (jvm != null) {
				return Collections.singleton(jvm);
			}
			throw new VMSelectionException("Couldn't find a JVM with PID "
					+ pid);
		} catch (NumberFormatException e) {
			/* Ignore. */
		}

		try {
			final Pattern p = compile_re(selector);
			final Set<JVM> matches = new HashSet<JVM>();
			for (final JVM jvm : vms.values()) {
				if (p.matcher(jvm.name()).find()) {
					matches.add(jvm);
				}
			}
			// Exclude ourselves from the matches.

			final Iterator<JVM> it = matches.iterator();
			while (it.hasNext()) {
				final JVM jvm = it.next();
				final VirtualMachine vm = VirtualMachine.attach(String
						.valueOf(jvm.pid()));
				try {
					if (vm.getSystemProperties().containsKey(MAGIC_KEY)) {
						it.remove();
						continue;
					}
				} finally {
					vm.detach();
				}
			}

			return matches;

		} catch (PatternSyntaxException e) {
			throw new VMSelectionException("Invalid pattern: " + selector, e);
		} catch (Exception e) {
			throw new VMSelectionException("Unexpected Exception", e);
		}

	}

	/**
	 * Returns a map from PID to JVM.
	 */
	public static HashMap<Integer, JVM> getJVMs() throws VMSelectionException {
		final HashMap<Integer, JVM> vms = new HashMap<Integer, JVM>();
		getMonitoredVMs(vms);
		getAttachableVMs(vms);
		return vms;
	}

	public static Set<JVM> getJVMs(String selector)
			throws VMSelectionException {
		return selectJVM(selector, getJVMs());
	}

	private static void getMonitoredVMs(final HashMap<Integer, JVM> out)
			throws VMSelectionException {
		try {
			MonitoredHost host = MonitoredHost
					.getMonitoredHost(new HostIdentifier((String) null));

			@SuppressWarnings("unchecked")
			final Set<Integer> vms = host.activeVms();
			for (final Integer pid : vms) {
				try {
					final VmIdentifier vmid = new VmIdentifier(pid.toString());
					final MonitoredVm vm = host.getMonitoredVm(vmid);
					out.put(pid, new JVM(pid, MonitoredVmUtil.commandLine(vm),
							ConnectorAddressLink.importFrom(pid)));
					vm.detach();
				} catch (Exception e) {
					Logger.getLogger(JMXUtils.class.getName()).log(
							Level.WARNING, "could not get vm", e);
				}
			}

		} catch (MonitorException e) {
			throw new VMSelectionException("", e);
		} catch (URISyntaxException e) {
			throw new VMSelectionException("", e);
		}
	}

	private static void getAttachableVMs(final HashMap<Integer, JVM> out) {
		for (final VirtualMachineDescriptor vmd : VirtualMachine.list()) {
			int pid;
			try {
				pid = Integer.parseInt(vmd.id());
			} catch (NumberFormatException e) {
				System.err.println("Ignoring invalid vmd.id(): " + vmd.id()
						+ ' ' + e.getMessage());
				continue;
			}
			if (out.containsKey(pid)) {
				continue;
			}
			try {
				final VirtualMachine vm = VirtualMachine.attach(vmd);
				out.put(pid, new JVM(pid, String.valueOf(pid), (String) vm
						.getAgentProperties().get(LOCAL_CONNECTOR_ADDRESS)));
				vm.detach();
			} catch (AttachNotSupportedException e) {
				System.err.println("VM not attachable: " + vmd.id() + ' '
						+ e.getMessage());
			} catch (IOException e) {
				System.err.println("Could not attach: " + vmd.id() + ' '
						+ e.getMessage());
			}
		}
	}
}
