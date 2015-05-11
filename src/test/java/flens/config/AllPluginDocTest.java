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

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import flens.core.Config;
import flens.core.Config.Option;
import flens.core.Flengine;
import flens.core.PluginRepo;
import flens.test.util.DefaultOverrides;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RunWith(Parameterized.class)
public class AllPluginDocTest {

    private class ArgumentCollector implements Answer<String> {

        private List<Option> options;
        private Map<String, Option> optmap;

        public ArgumentCollector(Config pr) {
            this.options = pr.getOptions();
            this.optmap = new HashMap<String, Option>();
            for (Option option : options) {
                optmap.put(option.getName(), option);
            }
        }

        @Override
        public String answer(InvocationOnMock invocation) throws Throwable {
            Object[] args = invocation.getArguments();
            String key = (String) args[0];
            Option opt = optmap.remove(key);
            if (opt == null) {
                fail("option not define in help: " + key);
            }
            return getDefaultFor(opt);
        }

        public void finish() {
            Set<String> names = optmap.keySet();
            if (!names.isEmpty()) {
                fail("options not used: " + names);
            }

        }

    }

    static PluginRepo pr = new PluginRepo();

    @Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> data() throws FileNotFoundException {
        List<Object[]> out = new LinkedList<>();
        PluginRepo limitedpr = new PluginRepo(new File("src/main/resources/plugins.json"));
        for (String name : limitedpr.names()) {
            out.add(new Object[] { name });
        }
        return out;
    }

    private Config plugin;
    private String pluginName;

    public AllPluginDocTest(String name) {
        this.pluginName = name;
        plugin = pr.get(name);
    }

    @Test
    public void testAll() {
        @SuppressWarnings("unchecked")
        Map<String, Object> mockmap = mock(Map.class);
        ArgumentCollector ac = new ArgumentCollector(plugin);
        when(mockmap.get(anyString())).thenAnswer(ac);
        when(mockmap.remove(anyString())).thenAnswer(ac);
        when(mockmap.isEmpty()).thenReturn(true);

        Flengine mocke = mock(Flengine.class);
        when(mocke.getPluginRepo()).thenReturn(pr);

        plugin.readConfigPart(pluginName, mockmap, mocke);

        ac.finish();
    }

    private String getDefaultFor(Option option) {
        return DefaultOverrides.getDefaultFor(pluginName, option);
    }

}
