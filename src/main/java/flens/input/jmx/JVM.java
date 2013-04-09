package flens.input.jmx;

import java.io.File;
import java.io.IOException;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

public final class JVM {
	

	final int pid;
	final String name;
	String address;

	public JVM(final int pid, final String name, final String address) {
		if (name.isEmpty()) {
			throw new IllegalArgumentException("empty name");
		}
		this.pid = pid;
		this.name = name;
		this.address = address;
	}

	public int pid() {
		return pid;
	}

	public String name() {
		return name;
	}

	public JMXServiceURL jmxUrl() {
		if (address == null) {
			ensureManagementAgentStarted();
		}
		try {
			return new JMXServiceURL(address);
		} catch (Exception e) {
			throw new RuntimeException("Error", e);
		}
	}

	public void ensureManagementAgentStarted() {
		if (address != null) { // already started
			return;
		}
		VirtualMachine vm;
		try {
			vm = VirtualMachine.attach(String.valueOf(pid));
		} catch (AttachNotSupportedException e) {
			throw new RuntimeException("Failed to attach to " + this, e);
		} catch (IOException e) {
			throw new RuntimeException("Failed to attach to " + this, e);
		}
		try {
			// java.sun.com/javase/6/docs/technotes/guides/management/agent.html#gdhkz
			// + code mostly stolen from JConsole's code.
			final String home = vm.getSystemProperties().getProperty(
					"java.home");

			// Normally in ${java.home}/jre/lib/management-agent.jar but might
			// be in ${java.home}/lib in build environments.

			String agent = home + File.separator + "jre" + File.separator
					+ "lib" + File.separator + "management-agent.jar";
			File f = new File(agent);
			if (!f.exists()) {
				agent = home + File.separator + "lib" + File.separator
						+ "management-agent.jar";
				f = new File(agent);
				if (!f.exists()) {
					throw new RuntimeException("Management agent not found");
				}
			}

			agent = f.getCanonicalPath();
			try {
				vm.loadAgent(agent, "com.sun.management.jmxremote");
			} catch (AgentLoadException e) {
				throw new RuntimeException("Failed to load the agent into "
						+ this, e);
			} catch (AgentInitializationException e) {
				throw new RuntimeException(
						"Failed to initialize the agent into " + this, e);
			}
			address = (String) vm.getAgentProperties().get(
					JMXUtils.LOCAL_CONNECTOR_ADDRESS);
		} catch (IOException e) {
			throw new RuntimeException(
					"Error while loading agent into " + this, e);
		} finally {
			try {
				vm.detach();
			} catch (IOException e) {
				throw new RuntimeException("Failed to detach from " + vm
						+ " = " + this, e);
			}
		}
		if (address == null) {
			throw new RuntimeException("Couldn't start the management agent.");
		}
	}

	public String toString() {
		return "JVM(" + pid + ", \"" + name + "\", "
				+ (address == null ? null : '"' + address + '"') + ')';
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + pid;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JVM other = (JVM) obj;
		if (pid != other.pid)
			return false;
		return true;
	}



	private MBeanServerConnection mbeansConnection; 
	private JMXConnector connection;
	
	public MBeanServerConnection getConnection() throws IOException{
		if(this.mbeansConnection !=null)
			return this.mbeansConnection;
		connection = JMXConnectorFactory.connect(jmxUrl());
		this.mbeansConnection = connection.getMBeanServerConnection();
		return this.mbeansConnection;
	}
	
	public void disconnect() throws IOException{
		if(connection!=null)
			connection.close();
	}
}
