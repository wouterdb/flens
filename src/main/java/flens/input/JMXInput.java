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

import com.sun.tools.classfile.Dependency.Finder;

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
			for (JVM jvm : jvms) {
				pump(jvm);
			}
		}

		private void pump(JVM jvm) {
			jvm.getConnection().queryMBeans(, query)
			
		}

		private void findJVMs() {
			try {
				Set<JVM> jvms = JMXUtils.getJVMs(selector);
				jvms.removeAll(this.jvms);
				this.jvms.addAll(jvms);
			} catch (VMSelectionException e) {
				err("problem finding JVM's", e);
			}
		}

		public void stop() {
			for (JVM vm : jvms) {
				try {
					vm.disconnect();
				} catch (IOException e) {
					err("exception trying to disconnect jmx",e);
				}
			}
		}

	}

	private int findJvmMultiplier;
	private String selector;
	private JMXInputWorker worker;

	public JMXInput(String name, Tagger tagger, String selector, int interval,
			int findJVMMultiplier) {
		super(name, tagger, interval);
		this.findJvmMultiplier = findJVMMultiplier;
		this.selector = selector;
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
