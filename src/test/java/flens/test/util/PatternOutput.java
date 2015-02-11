/*
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

package flens.test.util;

import static org.junit.Assert.*;

import flens.core.Constants;
import flens.core.Flengine;
import flens.core.Matcher;
import flens.core.Output;
import flens.core.Record;
import flens.core.util.AllMatcher;
import flens.output.util.AbstractPumpOutput;

import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PatternOutput extends AbstractPumpOutput implements Output, PatternStore {

    public PatternOutput(String name, String plugin) {
        super(name, plugin, new AllMatcher());
    }

    public PatternOutput(String name, String plugin, Matcher matcher) {
        super(name, plugin, matcher);
    }

    @Override
    public boolean canUpdateConfig() {
        return false;
    }

    @Override
    public void updateConfig(Flengine engine, Map<String, Object> tree) {
        throw new Error("This should NEVER be used in production");
    }

    private List<Pattern> sequence = new LinkedList<>();
    private LinkedList<Pair<Long, Record>> que = new LinkedList<>();

    public void addPattern(Pattern pattern) {
        sequence.add(pattern);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Record record = queue.take();
                sent++;
                que.add(Pair.of(System.nanoTime(), record));
            } catch (InterruptedException e) {
                // normal for stop
                stop();
            }

        }
    }

    public void analyze(float deltarate, int deltalength) {
        int current = 0;

        float prev = Float.MAX_VALUE;

        for (Pattern p : sequence) {
            if (Math.abs(p.msgrate - prev) < deltarate) {
                System.out.println("packet rates can not be distinguished" + p);
            }
            prev = p.msgrate;
        }

        for (Pair<Long, Record> rec : que) {
            System.out.println(rec.getKey() + "\t" + rec.getValue().getValues().get(Constants.TIME) + "\t"
                    + rec.getValue().getValues().get("message"));
        }

        for (Pattern p : sequence) {
            int expectedNroFPackets = p.getNrOfPackets();
            int deltapacks = (int) (deltalength * p.msgrate / 1000);

            int index;
            float rate = 0;

            if (p.warmup) {
                current += expectedNroFPackets;
                continue;
            }

            if (p.nonnormal) {
                if (deltalength != 0) {
                    throw new IllegalArgumentException("non normal but no fixed length, not implemented");
                }
                long now = que.get(current).getKey();

                long next = que.get(Math.min(current + p.getNrOfPackets(), que.size() - 1)).getKey();

                long time = next - now;
                rate = 1000000000.0f / time * p.getNrOfPackets();
                assertEquals(p.msgrate, rate, deltarate);

                current += p.getNrOfPackets();
                continue;
            }

            // normal
            for (index = 0; index < expectedNroFPackets + deltapacks && index + current + 1 < que.size(); index++) {
                long now = que.get(index + current).getKey();
                long next = que.get(index + current + 1).getKey();
                long time = next - now;
                rate = 1000000000.0f / time;

                if (!(Math.abs(p.msgrate - rate) < deltarate)) {
                    System.out.println(String.format(
                            "rate change at packet %d (pattern offset=%d) to %f (expected %f)",
                            index,current, rate, p.msgrate));
                    break;
                }

            }

            // last one is one short, due to interval calc
            if (index + current + 1 == que.size()) {
                assertEquals("incorrect nr of packets, got rate " + rate, p.getNrOfPackets(), index + 1, deltalength);
            } else {
                assertEquals("incorrect nr of packets, got rate " + rate + " (" + p.msgrate + ")", p.getNrOfPackets(),
                        index, deltalength);
            }

            current += index;
        }

    }

    public void analyzeContent() {
        int current = 0;

        for (Pattern p : sequence) {
            int expectedNroFPackets = p.getNrOfPackets();

            if (p.warmup) {
                current += expectedNroFPackets;
                continue;
            }

            for (int i = 0; i < p.getNrOfPackets(); i++) {

                assertEquals("content unequal at packet " + i + current, p.getMessage(i), que.get(current + i)
                        .getValue().getValues().get("message"));
            }
            current += p.getNrOfPackets();

        }

    }
}
