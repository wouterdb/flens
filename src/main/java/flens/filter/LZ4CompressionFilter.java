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
package flens.filter;

import flens.core.Matcher;
import flens.core.Record;
import flens.core.SelfReporter;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;
import flens.typing.MetricForm;
import flens.typing.MetricType;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;



/**
 * LZ4 compressor.
 *
 */
public class LZ4CompressionFilter extends AbstractFilter implements SelfReporter {

    private static final MetricType INTYPE = new MetricType("flens.LZ4.in", "byte", "flens", MetricForm.Counter, 0,
            Long.MAX_VALUE, true);
    private static final MetricType OUTTYPE = new MetricType("flens.LZ4.out", "byte", "flens", MetricForm.Counter, 0,
            Long.MAX_VALUE, true);
    private static final MetricType COUNTTYPE = new MetricType("flens.LZ4.count", "record", "flens",
            MetricForm.Counter, 0, Long.MAX_VALUE, true);
    private boolean discard;

    /**
     * @param name
     *            name under which this plugin is registered with the engine
     * @param plugin
     *            name of config that loaded this plugin (as registered in
     *            plugins.json)
     * @param tagger
     *            tagger used to mark output records
     * @param matcher
     *            matcher this filter should used to select recrods
     * @param prio
     *            plugin priority
     * @param infield
     *            field to take
     * @param outfield
     *            field to store result in
     * @param discard
     *            remove infield after filtering?
     */
    public LZ4CompressionFilter(String name, String plugin, Tagger tagger, Matcher matcher, int prio, String infield,
            String outfield, boolean discard) {
        super(name, plugin, tagger, matcher, prio);
        this.infield = infield;
        this.outfield = outfield;
        this.discard = discard;
    }

    private LZ4Factory factory = LZ4Factory.fastestJavaInstance();
    private String infield;
    private String outfield;

    AtomicLong bytesin = new AtomicLong();
    AtomicLong bytesout = new AtomicLong();
    AtomicLong count = new AtomicLong();

    @Override
    public Collection<Record> process(Record in) {
        LZ4Compressor compressor = factory.fastCompressor();

        byte[] data = in.getBytes(infield);

        byte[] out = new byte[compressor.maxCompressedLength(data.length) + 4];
        int compressedLength = compressor.compress(data, 0, data.length, out, 4);
        out = Arrays.copyOf(out, compressedLength + 4);
        addLength(out, data.length);

        bytesin.addAndGet(data.length);
        bytesout.addAndGet(compressedLength + 4);
        count.addAndGet(1);

        if (discard) {
            in.getValues().remove(infield);
        }
        in.getValues().put(outfield, out);
        tag(in);
        return Collections.emptyList();
    }

    private void addLength(byte[] data, int length) {
        data[0] = (byte) (length >> 24);
        data[1] = (byte) (length >> 16);
        data[2] = (byte) (length >> 8);
        data[3] = (byte) (length);
    }

    @Override
    public void report(Set<Record> out) {

        out.add(Record.createFromTypeAndInstance(bytesin.get(), INTYPE, getName()));
        out.add(Record.createFromTypeAndInstance(bytesout.get(), OUTTYPE, getName()));
        out.add(Record.createFromTypeAndInstance(count.get(), COUNTTYPE, getName()));

    }

    public long getRecordsSent() {
        return count.get();
    }

}
