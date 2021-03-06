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
        
package flens.input;

import flens.core.Flengine;
import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractPeriodicInput;
import flens.typing.MetricForm;
import flens.typing.MetricType;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class SpecInput extends AbstractPeriodicInput {

    private Map<String, Spec> allspecs = new HashMap<>();
    private List<Spec> specs;

    /**
     * @param name
     *            name under which this plugin is registered with the engine
     * @param plugin
     *            name of config that loaded this plugin (as registered in
     *            plugins.json)
     * @param tagger
     *            tagger used to mark output records
     * @param interval
     *            samlping interval in ms
     * @param specs
     *            which micro benchmarks to run, empty for all
     */
    public SpecInput(String name, String plugin, Tagger tagger, int interval, List<String> specs) {
        super(name, plugin, tagger, interval);
        if (interval < 1000) {
            throw new IllegalArgumentException("time under 1000ms is too short to run all tests");
        }
        List<Spec> allspecs = collectAllSpecs();
        for (Spec s : allspecs) {
            this.allspecs.put(s.getName(), s);
        }
        activate(specs);
    }

    /**
     * extend here!.
     *
     */
    protected List<Spec> collectAllSpecs() {
        List<Spec> specs = new LinkedList<Spec>();
        specs.add(new SpecDisk());
        specs.add(new SpecDiskRead());
        specs.add(new SpecCpu());
        specs.add(new SpecExec());
        specs.add(new SpecSleep());
        return specs;

    }

    private void activate(List<String> specs) {
        this.specs = new LinkedList<Spec>();

        if (specs.isEmpty()) {
            this.specs.addAll(allspecs.values());
        } else {

            for (String name : specs) {
                Spec current = this.allspecs.get(name);
                if (current == null) {
                    warn(String.format("spec %s not found, options are %s", name, allspecs.keySet().toString()));
                } else {
                    this.specs.add(current);
                }
            }
        }
    }

    public interface Spec {
        public void run() throws Exception;

        public String getName();
    }

    public class SpecDisk implements Spec {

        private static final int DISK_BYTES = 1000;
        private String metric = SpecInput.this.getName() + "." + getName();
        private MetricType mytype = new MetricType(metric, "ns", "disk", MetricForm.Absolute, 0, Integer.MAX_VALUE,
                true);

        @Override
        public void run() throws IOException {
            final long now = System.nanoTime();
            File file = File.createTempFile("flens-spec", "test");
            OutputStream out = new FileOutputStream(file);
            for (int i = 0; i < DISK_BYTES; i++) {
                out.write(i);
            }
            out.flush();
            out.close();
            long delta = System.nanoTime() - now;
            file.delete();
            dispatch(Record.forMetric(delta, mytype));
        }

        @Override
        public String getName() {
            return "write";
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    public class SpecDiskRead implements Spec {

        private static final int DISK_BYTES = 1000;
        private final String metric = SpecInput.this.getName() + "." + getName();
        private MetricType mytype = new MetricType(metric, "ns", "disk", MetricForm.Absolute, 0, Integer.MAX_VALUE,
                true);
        private final String file;

        protected SpecDiskRead() {
            String[] files = System.getProperty("sun.boot.class.path").split(":");
            int index = 0;
            for (; index < files.length; index++) {
                File file = new File(files[index]);
                if (file.canRead()) {
                    break;
                }
            }
            this.file = files[index];

        }

        @Override
        public void run() throws IOException {
            long now = System.nanoTime();
            File fh = new File(file);
            InputStream in = new FileInputStream(fh);
            byte[] bytes = new byte[DISK_BYTES];
            IOUtils.read(in, bytes);
            in.close();
            long delta = System.nanoTime() - now;
            dispatch(Record.forMetric(delta, mytype));
        }

        @Override
        public String getName() {
            return "read";
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    public class SpecExec implements Spec {

        private static final String CMD = "/usr/bin/true";
        private String metric = SpecInput.this.getName() + "." + getName();
        private MetricType mytype = 
                new MetricType(metric, "ns", "cpu", MetricForm.Absolute, 0, Integer.MAX_VALUE, true);

        @Override
        public void run() throws IOException, InterruptedException {
            ProcessBuilder pb = new ProcessBuilder(CMD);
            long now = System.nanoTime();
            pb.inheritIO();
            Process proc = pb.start();
            proc.waitFor();
            long delta = System.nanoTime() - now;
            dispatch(Record.forMetric(delta, mytype));
        }

        @Override
        public String getName() {
            return "exec";
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    public class SpecSleep implements Spec {

        private static final int interval = 100;
        private String metric = SpecInput.this.getName() + "." + getName();
        private MetricType mytype = new MetricType(metric, "ns", "cpu", MetricForm.Absolute, 0, interval * 1000000,
                true);

        @Override
        public void run() throws IOException, InterruptedException {

            long now = System.nanoTime();
            Thread.sleep(interval);
            long delta = System.nanoTime() - now - (interval * 1000000);
            dispatch(Record.forMetric(delta, mytype));
        }

        @Override
        public String getName() {
            return "sleep";
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    public class SpecCpu implements Spec {

        private static final int CPU_NUMBER = 66667;
        private String metric = SpecInput.this.getName() + "." + getName();
        private MetricType mytype = new MetricType(metric, "ns", "cpu", MetricForm.Absolute, 0, 100000, true);
        private long keeper;

        @Override
        public void run() throws IOException {
            keeper++;
            long now = System.nanoTime();
            keeper = factor(CPU_NUMBER);
            long delta = System.nanoTime() - now;
            dispatch(Record.forMetric(delta, mytype));
            delta = keeper;
        }

        @Override
        public String getName() {
            return "cpu";
        }

        @Override
        public String toString() {
            return getName();
        }

        private long factor(long number) {
            long out = 0;

            // for each potential factor i
            for (long i = 2; i <= number / i; i++) {

                // if i is a factor of N, repeatedly divide it out
                while (number % i == 0) {
                    out += i;
                    number = number / i;
                }
            }

            // if biggest factor occurs only once, n > 1
            if (number > 1) {
                out += number;
            }

            return out;
        }

    }

    @Override
    protected TimerTask getWorker() {
        return new TimerTask() {

            @Override
            public void run() {
                for (Spec spec : specs) {
                    try {
                        spec.run();
                    } catch (Exception e) {
                        err("spec test failed ", e);
                    }
                }

            }
        };
    }

    @Override
    public void writeConfig(Flengine engine, Map<String, Object> tree) {
        Map<String, Object> subtree = new HashMap<String, Object>();
        tree.put(getName(), subtree);
        subtree.put("interval", interval);
        tagger.outputConfig(subtree);
        List<String> currentspecs = new LinkedList<>();
        for (Spec spec : specs) {
            currentspecs.add(spec.getName());
        }
        subtree.put("tests", currentspecs);
    }

}
