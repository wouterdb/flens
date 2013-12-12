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
package flens.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import flens.util.NamedThreadFactory;

//TODO reconnect!
//FIXME: cleanup locking
public class Flengine {

	public class FilterWorker implements Runnable {

		private final Record record;

		public FilterWorker(Record record) {
			this.record = record;
		}

		public void run() {
			// based loosely on logstash
			Queue<Record> newrecords = new LinkedList<Record>();
			newrecords.add(record);
			int i = 0;
			while (!newrecords.isEmpty()) {
				Record current = newrecords.remove();
				synchronized (filters) {
					for (Filter f : filters) {
						if (f.getMatcher().matches(current)) {
							newrecords.addAll(f.process(current));
							if (current.getType() == null)
								break;
						}
					}
				}
				if (current.getType() != null)
					dispatch(current);

				i++;
				// FIXME make configurable
				if (i > 1000)
					throw new IllegalStateException(
							"filter loop is overflowing (1000 items producedfrom single record)"
									+ record);

			}

		}

		private void dispatch(Record current) {
			synchronized (outputs) {
				for (Output output : outputs) {
					if (output.getMatcher().matches(current))
						output.getOutputQueue().add(current);
				}
			}
		}

	}

	public class QueueWrapper implements BlockingQueue<Record> {

		public Record remove() {
			throw new UnsupportedOperationException();
		}

		public Record poll() {
			throw new UnsupportedOperationException();
		}

		public Record element() {
			throw new UnsupportedOperationException();
		}

		public Record peek() {
			throw new UnsupportedOperationException();
		}

		public int size() {
			return executor.getQueue().size();
		}

		public boolean isEmpty() {
			return executor.getQueue().isEmpty();
		}

		public Iterator<Record> iterator() {
			throw new UnsupportedOperationException();
		}

		public Object[] toArray() {
			throw new UnsupportedOperationException();
		}

		public <T> T[] toArray(T[] a) {
			throw new UnsupportedOperationException();
		}

		public boolean containsAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		public boolean addAll(Collection<? extends Record> c) {
			throw new UnsupportedOperationException();
		}

		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		public void clear() {
			throw new UnsupportedOperationException();
		}

		public boolean add(Record e) {
			executor.execute(wrap(e));
			return true;
		}

		public boolean offer(Record e) {
			executor.execute(wrap(e));
			return true;
		}

		public void put(Record e) throws InterruptedException {
			executor.execute(wrap(e));

		}

		public boolean offer(Record e, long timeout, TimeUnit unit)
				throws InterruptedException {
			executor.execute(wrap(e));
			return true;
		}

		public Record take() throws InterruptedException {
			throw new UnsupportedOperationException();
		}

		public Record poll(long timeout, TimeUnit unit)
				throws InterruptedException {
			throw new UnsupportedOperationException();
		}

		public int remainingCapacity() {
			return inqueue.remainingCapacity();
		}

		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		public boolean contains(Object o) {
			throw new UnsupportedOperationException();
		}

		public int drainTo(Collection<? super Record> c) {
			throw new UnsupportedOperationException();
		}

		public int drainTo(Collection<? super Record> c, int maxElements) {
			throw new UnsupportedOperationException();
		}

		private Runnable wrap(Record record) {
			return new FilterWorker(record);
		}

	}

	private final List<Input> inputs = new LinkedList<Input>();
	private final List<Output> outputs = new LinkedList<Output>();
	private final List<Filter> filters = new LinkedList<Filter>();
	private final Map<String, Integer> refcounts = new HashMap<>();

	private final BlockingQueue<Record> inqueue = new QueueWrapper();
	private boolean running;
	private ThreadPoolExecutor executor;

	private final Map<String, String> tags = new HashMap<String, String>();

	protected boolean count(String name) {
		synchronized (refcounts) {
			if (refcounts.containsKey(name)) {
				refcounts.put(name, refcounts.get(name) + 1);
				return false;
			} else {
				refcounts.put(name, 1);
				return true;
			}
		}

		// TODO: detect collisions
		// FIXME: locking is fishy
	}

	protected boolean decount(String name) {
		synchronized (refcounts) {
			int count = refcounts.get(name);

			if (count == 1) {
				refcounts.remove(name);
				return true;
			} else {
				refcounts.put(name, refcounts.get(name) - 1);
				return false;
			}

		}
		// FIXME: locking is fishy
	}

	public void addInput(Input inp) {
		if (count(inp.getName())) {
			synchronized (inputs) {
				inputs.add(inp);
				inp.setInputQueue(inqueue);
				if (running)
					inp.start();
			}
		}
	}

	protected void removeInput(Input inp) {
		if (decount(inp.getName())) {
			synchronized (inputs) {
				inputs.remove(inp);
				if (running) {
					inp.stop();
					try {
						inp.join();
					} catch (InterruptedException e) {
						Logger.getLogger(getClass().getName()).log(
								Level.SEVERE, "should not occur executor", e);
					}
				}
				inp.setInputQueue(null);
			}
		}
	}

	public void addOutput(Output outp) {
		if (count(outp.getName())) {
			synchronized (outputs) {
				outputs.add(outp);
				if (running)
					outp.start();
			}
		}
	}

	protected void removeOutput(Output outp) {
		if (decount(outp.getName())) {
			synchronized (outputs) {
				outputs.remove(outp);
				if (running)
					outp.stop();
			}
		}
	}

	/**
	 * add filter behind the others
	 */
	public void addFilter(Filter f) {
		if (count(f.getName())) {
			synchronized (filters) {
				filters.add(f);
				Collections.sort(filters, new Comparator<Filter>() {

					@Override
					public int compare(Filter o1, Filter o2) {
						return o1.priority() - o2.priority();
					}
				});
			}
		}
	}

	/**
	 * add filter behind the others
	 */
	protected void removeFilter(Filter f) {
		if (decount(f.getName())) {
			synchronized (filters) {
				filters.remove(f);
			}
		}
	}

	public void start() {
		running = true;
		for (Output output : outputs) {
			output.start();
		}

		// FIXME: make configurable
		// TODO: is this the best way?
		executor = new ThreadPoolExecutor(1, 8, 200, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory(
						"flens-mainloop"));

		for (Input input : inputs) {
			input.start();
		}
		
		for (QueryHandler input : handlers) {
			input.start();
		}

	}

	public void stop() {
		running = false;
		for (Input input : inputs) {
			input.stop();
		}

		try {
			for (Input input : inputs) {
				System.out.println("joining");
				input.join();
			}
		} catch (InterruptedException e) {
			// TODO what to do now????
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"should not occur executor", e);
			System.exit(1);
		}

		try {

			executor.shutdown();
			executor.awaitTermination(10, TimeUnit.SECONDS);

		} catch (InterruptedException e) {
			// TODO what to do now????
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"cloud not close executor", e);
			System.exit(1);
		}

		for (Output output : outputs) {
			while (output.getOutputQueue().size() > 0) {
				// TODO: best option?
				Thread.yield();
			}
			output.stop();
		}
	}

	public void report(Set<Record> out) {

		out.add(new Record("flens.q-in-size", executor.getQueue().size()));
		for (Output o : outputs) {
			out.add(new Record(String.format("flens.q-%s-size", o.getName()), o
					.getOutputQueue().size()));
			out.add(new Record(String.format("flens.q-%s-sent", o.getName()), o
					.getRecordsSent()));
			out.add(new Record(String.format("flens.q-%s-lost", o.getName()), o
					.getRecordsLost()));
		}
		
		for (Input o : inputs) {
			out.add(new Record(String.format("flens.q-%s-sent", o.getName()), o
					.getRecordsSent()));
		}
		
		out.add(new Record("flens.exec-threads-active", executor
				.getActiveCount()));
		out.add(new Record("flens.exec-threads-live", executor.getPoolSize()));
		out.add(new Record("flens.exec-seen", executor.getCompletedTaskCount()));
	}

	public void remove(String name) {
		for (Input inp : inputs) {
			if (inp.getName().equals(name)) {
				removeInput(inp);
				return;
			}
		}
		for (Filter inp : filters) {
			if (inp.getName().equals(name)) {
				removeFilter(inp);
				return;
			}
		}
		for (Output inp : outputs) {
			if (inp.getName().equals(name)) {
				removeOutput(inp);
				return;
			}
		}
		for (QueryHandler inp : handlers) {
			if (inp.getName().equals(name)) {
				removeHandler(inp);
				return;
			}
		}

	}

	public void addTags(Map<String, String> tags) {
		this.tags.putAll(tags);
	}

	public Map<String, String> getTags() {
		return this.tags;
	}

	private List<QueryHandler> handlers = new LinkedList<>();

	public void addHandler(QueryHandler qh) {
		if (count(qh.getName())) {
			synchronized (handlers) {
				handlers.add(qh);
				if (running)
					qh.start();
			}
		}
	}

	protected void removeHandler(QueryHandler inp){
		if (decount(inp.getName())) {
			synchronized (handlers) {
				handlers.remove(inp);
				inp.stop();
			}
		}
	}
	
	public List<QueryHandler> getHandler(Query q){
		synchronized (handlers) {
			List<QueryHandler> qhs = new LinkedList<>(); 
			for (QueryHandler qh : handlers) {
				if(qh.canHandle(q)){
					qhs.add(qh);
				}
			}
			return qhs;
		}
	}
}
