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
        
package flens.pairs;

import flens.core.Flengine;
import flens.core.PluginRepo;
import flens.core.util.InputTagger;
import flens.core.util.StandardMatcher;
import flens.core.util.StandardTagger;
import flens.filter.BatchingFilter;
import flens.filter.UnBatchingFilter;
import flens.input.util.InputQueueExposer;
import flens.test.util.Pattern;
import flens.test.util.PatternInput;
import flens.test.util.PatternOutput;

import org.junit.Test;

import java.util.Collections;

public class Batchers {

    @Test
    public void testBatcherPair() throws InterruptedException {
        PatternInput pinp = new PatternInput("pinp", "pinp", new InputTagger("", "test",
                Collections.<String>emptyList()));
        PatternOutput poutp = new PatternOutput("poutp", "poutp", new StandardMatcher("unbatched",
                Collections.<String>emptyList()));
        PatternOutput pmidp = new PatternOutput("pmidp", "poutp", new StandardMatcher("batched",
                Collections.<String>emptyList()));

        InputQueueExposer inexp = new InputQueueExposer("batching-in", null, new InputTagger("", "batched",
                Collections.<String>emptyList()));
        BatchingFilter bf = new BatchingFilter("batching", "batching", new StandardMatcher("test",
                Collections.<String>emptyList()), inexp, 10, 97);
        UnBatchingFilter ubf = new UnBatchingFilter("unb", "unb", new StandardTagger("", "unbatched",
                Collections.<String>emptyList(), Collections.<String>emptyList()), new StandardMatcher("batched",
                Collections.<String>emptyList()), 1, true);

        Flengine testenFlengine = new Flengine(new PluginRepo());
        testenFlengine.setPoolSize(1);

        testenFlengine.addInput(pinp);
        testenFlengine.addInput(inexp);
        testenFlengine.addOutput(poutp);
        testenFlengine.addOutput(pmidp);
        testenFlengine.addOutput(bf);
        testenFlengine.addFilter(ubf);

        loadPattern1(pinp, poutp, pmidp);

        testenFlengine.start();
        pinp.join();
        testenFlengine.stop();

        System.out.println(pinp.getRecordsSent());
        System.out.println(inexp.getRecordsSent());
        System.out.println(bf.getRecordsSent());
        System.out.println(pmidp.getRecordsSent());
        System.out.println(poutp.getRecordsSent());

        pmidp.analyze(3.0f, 0);
        poutp.analyze(3.0f, 0);

    }

    @Test
    public void testBatcherPair2() throws InterruptedException {
        PatternInput pinp = new PatternInput("pinp", "pinp", new InputTagger("", "test",
                Collections.<String>emptyList()));
        PatternOutput poutp = new PatternOutput("poutp", "poutp", new StandardMatcher("unbatched",
                Collections.<String>emptyList()));
        PatternOutput pmidp = new PatternOutput("pmidp", "poutp", new StandardMatcher("batched",
                Collections.<String>emptyList()));

        InputQueueExposer inexp = new InputQueueExposer("batching-in", null, new InputTagger("", "batched",
                Collections.<String>emptyList()));
        BatchingFilter bf = new BatchingFilter("batching", "batching", new StandardMatcher("test",
                Collections.<String>emptyList()), inexp, 10, 97);
        UnBatchingFilter ubf = new UnBatchingFilter("unb", "unb", new StandardTagger("", "unbatched",
                Collections.<String>emptyList(), Collections.<String>emptyList()), new StandardMatcher("batched",
                Collections.<String>emptyList()), 1, true);

        Flengine testenFlengine = new Flengine(new PluginRepo());
        testenFlengine.setPoolSize(1);

        testenFlengine.addInput(pinp);
        testenFlengine.addInput(inexp);
        testenFlengine.addOutput(poutp);
        testenFlengine.addOutput(pmidp);
        testenFlengine.addOutput(bf);
        testenFlengine.addFilter(ubf);

        loadPattern2(pinp, poutp, pmidp);

        testenFlengine.start();
        pinp.join();
        Thread.sleep(1000);
        testenFlengine.stop();

        System.out.println(pinp.getRecordsSent());
        System.out.println(inexp.getRecordsSent());
        System.out.println(bf.getRecordsSent());
        System.out.println(pmidp.getRecordsSent());
        System.out.println(poutp.getRecordsSent());

        pmidp.analyze(10.0f, 0);
        poutp.analyze(3.0f, 0);

    }

    private void loadPattern1(PatternInput pinp, PatternOutput poutp, PatternOutput pmidp) {
        // warmup
        pinp.addPattern(new Pattern());
        pmidp.addPattern(new Pattern(1000, 10, "warmup", false, true));
        poutp.addPattern(new Pattern());

        // join per time
        pinp.addPattern(new Pattern(5000, 40, "per time"));
        pmidp.addPattern(new Pattern(5000, 10, "per time"));
        poutp.addPattern(new Pattern(5000, 40, "per time", true));

    }

    private void loadPattern2(PatternInput pinp, PatternOutput poutp, PatternOutput pmidp) {
        // warmup
        pinp.addPattern(new Pattern());
        pmidp.addPattern(new Pattern(1000, 10, "warmup", false, true));
        poutp.addPattern(new Pattern());

        // join per batch
        pinp.addPattern(new Pattern(5000, 1000, "per batch"));
        pmidp.addPattern(new Pattern(5000, 100, "per batch"));
        poutp.addPattern(new Pattern(5000, 1000, "per batch", true));
    }

}
