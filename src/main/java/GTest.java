import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.management.JMX;
import javax.management.remote.JMXServiceURL;

import com.nflabs.Grok.Grok;
import com.nflabs.Grok.Match;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;
import sun.management.ConnectorAddressLink;

class GTest {

	

	public static void main(String[] args) throws Throwable {
		Grok g = new Grok();
		String example = "INFO: workflow 68fac406-78d1-4ffe-923f-a824f9f447fa took: 389163 by jos";

		String pat = "INFO: workflow %{UUID:temporalScope} took: %{INT:value} by %{USERNAME:tenant}";
		
		g.addPatternFromReader(new InputStreamReader(GTest.class.getResourceAsStream("base")));
		System.out.println(g.compile(pat));
		Match m = g.match(example);
		m.captures();
		System.out.println(m.toMap());
	}


}