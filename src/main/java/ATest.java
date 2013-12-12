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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.management.JMX;
import javax.management.remote.JMXServiceURL;

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

class ATest implements Runnable {

	private File f;

	public ATest(File f) {
		this.f = f;
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		File f = File.createTempFile("pre", "post");

		FileWriter w = new FileWriter(f);
		w.write("dmngle\n");

		new Thread(new ATest(f)).start();
		
		for (int i = 0; i < 10; i++) {
			w.write(""+i);
			w.append("\n");
			Thread.sleep(100);
		}
		System.out.println("close");
		w.close();
	}

	@Override
	public void run() {

		try {
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			do {
				line = br.readLine();
				System.out.println(line);
			} while (line != null);
			br.close();
		} catch (IOException E) {
			E.printStackTrace();
		}
		System.out.println("done");
	}
}