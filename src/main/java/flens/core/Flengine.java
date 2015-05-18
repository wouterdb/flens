/*
 *
 *     Copyright 2013-2015 KU Leuven Research and Development - iMinds - Distrinet
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

import flens.typing.MetricForm;
import flens.util.NamedThreadFactory;

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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Flengine {

    private class FilterWorker implements Runnable {

        private final Record record;

        public FilterWorker(Record record) {
            this.record = record;
        }

        public void run() {
            // based loosely on logstash
            Queue<Record> newrecords = new LinkedList<Record>();
            newrecords.add(record);
            int loopcounter = 0;
            while (!newrecords.isEmpty()) {
                Record current = newrecords.remove();

                for (Filter f : filters) {
                    if (f.getMatcher().matches(current)) {
                        newrecords.addAll(f.process(current));
                        if (current.getType() == null) {
                            break;
                        }
                    }
                }

                if (current.getType() != null) {
                    dispatch(current);
                }

                loopcounter++;
                // FIXME make configurable
                if (loopcounter > 1000) {
                    throw new IllegalStateException(
                            "filter loop is overflowing (1000 items produced from single record)" + record);
                }

            }

        }

        private void dispatch(Record current) {
            synchronized (outputs) {
                for (Output output : outputs) {
                    if (output.getMatcher().matches(current)) {
                        output.getOutputQueue().add(current);
                    }
                }
            }
        }

    }

    public class QueueWrapper implements BlockingQueue<Record> {

        public void put(Record rec) throws InterruptedException {
            executor.execute(wrap(rec));
        }

        public Record remove() {
            throw new UnsupportedOperationException();
        }

        public boolean remove(Object object) {
            throw new UnsupportedOperationException();
        }

        public Record poll() {
            throw new UnsupportedOperationException();
        }

        public Record poll(long timeout, TimeUnit unit) throws InterruptedException {
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

        public <T> T[] toArray(T[] ar) {
            throw new UnsupportedOperationException();
        }

        public boolean containsAll(Collection<?> co) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection<? extends Record> co) {
            throw new UnsupportedOperationException();
        }

        public boolean removeAll(Collection<?> co) {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection<?> co) {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            throw new UnsupportedOperationException();
        }

        public boolean add(Record rec) {
            executor.execute(wrap(rec));
            return true;
        }

        public boolean offer(Record rec) {
            executor.execute(wrap(rec));
            return true;
        }

        public boolean offer(Record rec, long timeout, TimeUnit unit) throws InterruptedException {
            executor.execute(wrap(rec));
            return true;
        }

        public Record take() throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        public int remainingCapacity() {
            return inqueue.remainingCapacity();
        }

        public boolean contains(Object object) {
            throw new UnsupportedOperationException();
        }

        public int drainTo(Collection<? super Record> co) {
            throw new UnsupportedOperationException();
        }

        public int drainTo(Collection<? super Record> co, int maxElements) {
            throw new UnsupportedOperationException();
        }

        private Runnable wrap(Record record) {
            return new FilterWorker(record);
        }

    }

    private final List<Input> inputs = new LinkedList<Input>();
    private final List<Output> outputs = new LinkedList<Output>();
    private final List<Filter> filters = new CopyOnWriteArrayList<Filter>();
    private final Map<String, Integer> refcounts = new HashMap<>();

    private final BlockingQueue<Record> inqueue = new QueueWrapper();
    private boolean running;
    private ThreadPoolExecutor executor;

    private PluginRepo pr;

    public Flengine(PluginRepo pr) {
        this.pr = pr;
    }

    public PluginRepo getPluginRepo() {
        return pr;
    }

    private final Map<String, String> tags = new HashMap<String, String>();
    private int poolmaxsize = 8;

    public void setPoolSize(int poolsize) {
        this.poolmaxsize = poolsize;
    }

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

    /**
     * add a new input plugin to the engine
     */
    public void addInput(Input inp) {
        if (count(inp.getName())) {
            synchronized (inputs) {
                inputs.add(inp);
                inp.setInputQueue(inqueue);
                if (running) {
                    inp.start();
                }
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
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "should not occur executor", e);
                    }
                }
                inp.setInputQueue(null);
            }
        }
    }

    /**
     * Add an ouptut plugin.
     */
    public void addOutput(Output outp) {
        if (count(outp.getName())) {
            synchronized (outputs) {
                outputs.add(outp);
                if (running) {
                    outp.start();
                }
            }
        }
    }

    protected void removeOutput(Output outp) {
        if (decount(outp.getName())) {
            synchronized (outputs) {
                outputs.remove(outp);
                if (running) {
                    outp.stop();
                }
            }
        }
    }

    /**
     * add filter behind the others.
     */
    public void addFilter(Filter filter) {
        if (count(filter.getName())) {
            // filters is locked for modification, but is itself CoppyOnWrite,
            // and can be used while locked
            synchronized (filters) {
                int idx = Collections.binarySearch(filters, filter, new Comparator<Filter>() {

                    @Override
                    public int compare(Filter o1, Filter o2) {
                        return o1.priority() - o2.priority();
                    }
                });

                if (idx < 0) {
                    idx = -(idx + 1);
                }
                filters.add(idx, filter);

            }
        }
    }

    protected void removeFilter(Filter filter) {
        if (decount(filter.getName())) {
            synchronized (filters) {
                // filters is locked for modification, but is itself
                // CoppyOnWrite,
                // and can be used while locked
                filters.remove(filter);
            }
        }
    }

    /**
     * start the engine.
     */
    public void start() {
        running = true;
        for (Output output : outputs) {
            output.start();
        }

        // FIXME: make configurable
        // TODO: is this the best way?
        executor = new ThreadPoolExecutor(1, poolmaxsize, 200, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory("flens-mainloop"));

        for (Input input : inputs) {
            input.start();
        }

        for (QueryHandler input : handlers) {
            input.start();
        }

    }

    /**
     * stop the engine.
     */
    public void stop() {
        stop(true);
    }
    
    public void stop(boolean drainout) {
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
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "should not occur executor", e);
            throw new Error(e);
        }

        try {

            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            // TODO what to do now????
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "cloud not close executor", e);
            throw new Error(e);
        }

        
        for (Output output : outputs) {
            while (drainout &&  output.getOutputQueue().size() > 0) {
                // TODO: best option?
                Thread.yield();
            }
            output.stop();
        }

        try {
            for (Output output : outputs) {

                output.join();

            }
        } catch (InterruptedException e) {
            // TODO what to do now????
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "cloud not close output", e);
            throw new Error(e);
        }
    }

    private Record getQRecord(String sub, String instance, long value, MetricForm form) {
        return Record.forMetricWithInstance("flens.q-" + sub, instance, value, "record", "flens.q", form, "[0,[");
    }

    /**
     * add a full report on queue statistics to the given set of records.
     */
    public void report(Set<Record> out) {

        out.add(getQRecord("size", "in", executor.getQueue().size(), MetricForm.Gauge));
        for (Output o : outputs) {
            out.add(getQRecord("size", o.getName(), o.getOutputQueue().size(), MetricForm.Gauge));
            out.add(getQRecord("sent", o.getName(), o.getRecordsSent(), MetricForm.Counter));
            out.add(getQRecord("lost", o.getName(), o.getRecordsLost(), MetricForm.Counter));
        }

        for (Input o : inputs) {
            out.add(getQRecord("sent", o.getName(), o.getRecordsSent(), MetricForm.Counter));
        }

        out.add(Record.forMetric("flens.exec-threads-active", executor.getActiveCount(), "thread", "flens.executor",
                MetricForm.Gauge, "[0,["));
        out.add(Record.forMetric("flens.exec-threads-live", executor.getPoolSize(), "thread", "flens.executor",
                MetricForm.Gauge, "[0,["));
        out.add(Record.forMetric("flens.exec-seen", executor.getCompletedTaskCount(), "record", "flens.executor",
                MetricForm.Counter, "[0,["));

        for (SelfReporter r : getSelfReporters()) {
            r.report(out);
        }
    }

    protected List<SelfReporter> getSelfReporters() {
        LinkedList<SelfReporter> sr = new LinkedList<>();
        for (Input inp : inputs) {
            if (inp instanceof SelfReporter) {
                sr.add((SelfReporter) inp);
            }
        }
        for (Filter inp : filters) {
            if (inp instanceof SelfReporter) {
                sr.add((SelfReporter) inp);
            }
        }
        for (Output inp : outputs) {
            if (inp instanceof SelfReporter) {
                sr.add((SelfReporter) inp);
            }
        }
        for (QueryHandler inp : handlers) {
            if (inp instanceof SelfReporter) {
                sr.add((SelfReporter) inp);
            }
        }
        return sr;
    }

    /**
     * Remove a plugin with a given name.
     * 
     * @param name
     *            name of the plugin to be removed
     */
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

    /**
     * Add a query handler.
     * 
     */
    public void addHandler(QueryHandler qh) {
        if (count(qh.getName())) {
            synchronized (handlers) {
                handlers.add(qh);
                if (running) {
                    qh.start();
                }
            }
        }
    }

    protected void removeHandler(QueryHandler inp) {
        if (decount(inp.getName())) {
            synchronized (handlers) {
                handlers.remove(inp);
                inp.stop();
            }
        }
    }

    /**
     * return all query handlers able to handle the given query.
     */
    public List<QueryHandler> getHandler(Query query) {
        synchronized (handlers) {
            List<QueryHandler> qhs = new LinkedList<>();
            for (QueryHandler qh : handlers) {
                if (qh.canHandle(query)) {
                    qhs.add(qh);
                }
            }
            return qhs;
        }
    }

    /**
     * Dump the engines running config into the map.
     */
    public void dumpConfig(Map<String, Object> config) {
        config.put("input", dumpInputs(new HashMap<String, Object>()));
        config.put("filter", dumpFilters(new HashMap<String, Object>()));
        config.put("output", dumpOutputs(new HashMap<String, Object>()));
        config.put("query", dumpQueryHandlers(new HashMap<String, Object>()));

    }

    private Map<String, Object> dumpQueryHandlers(Map<String, Object> tree) {
        synchronized (handlers) {
            for (QueryHandler inp : handlers) {
                inp.writeConfig(this, tree);
            }
        }
        return tree;
    }

    private Map<String, Object> dumpOutputs(Map<String, Object> tree) {
        synchronized (outputs) {
            for (Output inp : outputs) {
                inp.writeConfig(this, tree);
            }
        }
        return tree;
    }

    private Map<String, Object> dumpFilters(Map<String, Object> tree) {
        synchronized (filters) {
            // filters is locked for modification, but is itself CoppyOnWrite,
            // and can be used while locked
            for (Filter inp : filters) {
                inp.writeConfig(this, tree);
            }
        }
        return tree;
    }

    private Map<String, Object> dumpInputs(Map<String, Object> tree) {

        synchronized (inputs) {
            for (Input inp : inputs) {
                inp.writeConfig(this, tree);
            }
        }
        return tree;

    }

    public int getInputSize() {
        return inputs.size();
    }

    public int getOutputSize() {
        return outputs.size();
    }

    public int getFilterSize() {
        return filters.size();
    }
}
