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