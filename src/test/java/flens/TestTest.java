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
package flens;


import flens.core.Flengine;
import flens.core.PluginRepo;
import flens.core.util.InputTagger;
import flens.test.util.Pattern;
import flens.test.util.PatternInput;
import flens.test.util.PatternOutput;
import flens.test.util.PatternStore;

import org.junit.Test;

import java.util.Collections;


public class TestTest {

    @Test
    public void testPatternStore() throws InterruptedException {
        PatternInput pinp = new PatternInput("pinp", "pinp", new InputTagger("", "test",
                Collections.<String>emptyList()));
        PatternOutput poutp = new PatternOutput("poutp", "poutp");

        load(pinp);
        load(poutp);

        Flengine testenFlengine = new Flengine(new PluginRepo());

        testenFlengine.addInput(pinp);
        testenFlengine.addOutput(poutp);

        testenFlengine.start();
        pinp.join();
        testenFlengine.stop();
        poutp.analyze(8.0f, 0);
    }

    private void load(PatternStore st) {
        st.addPattern(new Pattern());
        st.addPattern(new Pattern(3000, 10, "test a "));
        st.addPattern(new Pattern(3000, 1, "test b "));
    }
}
