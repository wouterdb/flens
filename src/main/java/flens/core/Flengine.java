package flens.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO reconnect!
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
				for (Filter f : filters) {
					if (f.getMatcher().matches(current)) {
						newrecords.addAll(f.process(current));
						if (current.getType() == null)
							break;
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
			for (Output output : outputs) {
				if (output.getMatcher().matches(current))
					output.getOutputQueue().add(current);
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

	private final BlockingQueue<Record> inqueue = new QueueWrapper();
	private boolean running;
	private ThreadPoolExecutor executor;

	public void addInput(Input inp) {
		if (running)
			throw new IllegalStateException(
					"engine running, can not add inputs");
		inputs.add(inp);
		inp.setInputQueue(inqueue);
	}

	public void addOutput(Output outp) {
		if (running)
			throw new IllegalStateException(
					"engine running, can not add outputs");
		outputs.add(outp);
	}

	/**
	 * add filter behind the others
	 */
	public void addFilter(Filter f) {
		if (running)
			throw new IllegalStateException(
					"engine running, can not add filters");
		filters.add(f);
	}

	public void start() {
		for (Output output : outputs) {
			output.start();
		}

		// FIXME: make configurable
		// TODO: is this the best way?
		executor = new ThreadPoolExecutor(1, 8, 200, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());

		for (Input input : inputs) {
			input.start();
		}

	}

	public void stop() {
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

	public void report(Record out) {

		Map<String, Object> values = out.getValues();

		values.put("fles.q-in-size", executor.getQueue().size());
		for (Output o : outputs) {
			values.put(String.format("q-%s-size", o.getName()), o
					.getOutputQueue().size());
		}
		values.put("fles.exec-threads-active", executor.getActiveCount());
		values.put("fles.exec-threads-live", executor.getPoolSize());
		values.put("fles.exec-seen", executor.getCompletedTaskCount());
	}
}
