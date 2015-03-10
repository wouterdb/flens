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

package flens.config;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import flens.core.ConfigBuilder;
import flens.core.Flengine;
import flens.core.Record;
import flens.core.util.AllMatcher;
import flens.core.util.TypeTagger;
import flens.input.util.InputQueueExposer;
import flens.output.util.OutputQueueExposer;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

public class ExpressionMatcherTest {

    @Test
    public void test() throws FileNotFoundException, InterruptedException {
        ConfigBuilder cb = new ConfigBuilder(new FileReader("src/test/resources/conf/matcherTest.json"));
        cb.run();
        Flengine fl = cb.getEngine();
        InputQueueExposer ipq = new InputQueueExposer("in", "iqe", new TypeTagger("", "in"));
        fl.addInput(ipq);
        OutputQueueExposer opq = new OutputQueueExposer(new AllMatcher(), "out");
        fl.addOutput(opq);
        fl.start();

        ipq.send(createWithFields("test1", "keep", ""));
        ipq.send(createWithFields("test1", "drop", ""));
        ipq.send(createWithFields("test1", "fla", ""));
        ipq.send(createWithFields("test1", "drop", ""));
        
        Thread.sleep(100);
        List<Record> output = new LinkedList<>();
        opq.getOutputQueue().drainTo(output);
        fl.stop();

        assertThat(output.size(), is(2));
    }

    private Record createWithFields(String msg, String field, String value) {
        Record out = Record.createWithMessage(msg);
        out.getValues().put(field, value);
        return out;
    }

}
