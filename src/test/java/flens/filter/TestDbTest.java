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

package flens.filter;

import static org.junit.Assert.assertEquals;

import flens.core.Flengine;
import flens.core.PluginRepo;
import flens.core.Record;
import flens.core.Tagger;
import flens.core.util.AllMatcher;
import flens.core.util.InputTagger;
import flens.core.util.StandardMatcher;
import flens.core.util.TypeTagger;
import flens.input.GrepInput;
import flens.output.util.OutputQueueExposer;
import flens.typing.LogTypesDb;

import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TestDbTest {

    @Test
    public void test() throws Exception {
        LogTypesDb ltdb = new LogTypesDb("src/test/resources/logtypes", false);
        assertEquals(ltdb.getAll().size(), 2);
    }

    @Test
    public void testrefresh() throws Exception {
        LogTypesDb ltdb = new LogTypesDb("src/test/resources/logtypes", true);
        assertEquals(ltdb.getAll().size(), 2);
    }

    @Test
    public void testWorkings() throws InterruptedException {
        GrepInput gin = new GrepInput("testinput", "grep",
                new InputTagger("", "logs", Collections.<String>emptyList()),
                "src/test/resources/logtypes/teststream.data", ".*", false);
        JSonDecoder jd = new JSonDecoder("decoder", "decoder", Tagger.empty, new AllMatcher(), 5, false);
        LogTypesDb db = new LogTypesDb("src/test/resources/logtypes", false);
        LogTypeChecker ltc = new LogTypeChecker("xfilter", "log-type-checker", new AllMatcher(), 14, new TypeTagger("",
                "matched"), Tagger.empty, db, "src/test/resources/logtypes", false);
        OutputQueueExposer poutp = new OutputQueueExposer(new StandardMatcher("matched",
                Collections.<String>emptyList()), "testout");

        Flengine testenFlengine = new Flengine(new PluginRepo());

        testenFlengine.addInput(gin);
        testenFlengine.addFilter(jd);
        testenFlengine.addFilter(ltc);
        testenFlengine.addOutput(poutp);

        testenFlengine.start();
        while (gin.getRecordsSent() < 5) {
            Thread.sleep(10);
        }

        testenFlengine.stop(false);
        List<Record> output = new LinkedList<>();
        poutp.getOutputQueue().drainTo(output);
        System.out.println(output);
        assertEquals(output.size(), 2);
        assertEquals(output.get(0).get("sid"), 7895);
        assertEquals(output.get(0).get("type"), "log.login");
    }

}
