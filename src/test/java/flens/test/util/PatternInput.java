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

import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractActiveInput;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PatternInput extends AbstractActiveInput implements PatternStore {

    ReentrantLock lock = new ReentrantLock();
    Condition cond = lock.newCondition();

    public PatternInput(String name, String plugin, Tagger tagger) {
        super(name, plugin, tagger);
    }

    private List<Pattern> sequence = new LinkedList<>();

    @Override
    public void run() {
        long start = System.nanoTime();

        lock.lock();

        try {
            for (Pattern p : sequence) {

                int nrofpackets = p.getNrOfPackets();
                long delay = p.length * 1000000L / nrofpackets;
                for (int i = 0; i < nrofpackets; i++) {
                    dispatch(Record.forLog(p.getMessage(i)));
                    // long now = System.nanoTime();
                    // cond.awaitNanos(start + delay - now);
                    // System.out.println(start + delay - now);
                    // Thread.sleep(start + delay - now);
                    while (System.nanoTime() < start + delay) {

                    }
                    start += delay;
                }

            }

            /*
             * catch (InterruptedException e) { System.out.println(e); }
             */
        } finally {

            lock.unlock();

            running = false;
        }
        
        
    }

    public void addPattern(Pattern pattern) {
        sequence.add(pattern);
    }

}
