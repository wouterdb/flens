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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import flens.core.Config;
import flens.core.Config.Option;
import flens.core.Filter;
import flens.core.Flengine;
import flens.core.Input;
import flens.core.Output;
import flens.core.Plugin;
import flens.core.PluginRepo;
import flens.core.QueryHandler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RunWith(Parameterized.class)
public class AllPluginRoundTest {

    static PluginRepo pr = new PluginRepo();

    @Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> data() {
        List<Object[]> out = new LinkedList<>();
        for (String name : pr.names()) {
            out.add(new Object[] { name });
        }
        //out.add(new Object[] { "metric-type-check" });
        return out;
    }

    private Config plugin;
    private String pluginName;

    public AllPluginRoundTest(String name) {
        this.pluginName = name;
        plugin = pr.get(name);
    }

    @Test
    public void testAll() {
        Map<String, Object> defaults = constructDefault(plugin);

        Map<String, Object> config1 = configRoundTrip(defaults);
        org.junit.Assert.assertFalse(config1.size() == 0);

        if (config1.equals(defaults)) {
            return;
        }
        
        Map<String, Object> config2 = configRoundTrip(config1);
        assertThat(config2, equalTo(config1));

        Map<String, Object> config3 = configRoundTrip(config2);
        assertThat(config3, equalTo(config2));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> configRoundTrip(Map<String, Object> defaults) {
        Flengine mocke = mock(Flengine.class);
        when(mocke.getPluginRepo()).thenReturn(pr);
        plugin.readConfigPart(pluginName, new HashMap<>(defaults), mocke);

        ArgumentCaptor<Filter> filtercap = ArgumentCaptor.forClass(Filter.class);
        verify(mocke, atLeast(0)).addFilter(filtercap.capture());

        ArgumentCaptor<Input> incap = ArgumentCaptor.forClass(Input.class);
        verify(mocke, atLeast(0)).addInput(incap.capture());

        ArgumentCaptor<Output> outcap = ArgumentCaptor.forClass(Output.class);
        verify(mocke, atLeast(0)).addOutput(outcap.capture());

        ArgumentCaptor<QueryHandler> qcap = ArgumentCaptor.forClass(QueryHandler.class);
        verify(mocke, atLeast(0)).addHandler(qcap.capture());

        List<Plugin> plugins = new LinkedList<Plugin>();
        plugins.addAll(filtercap.getAllValues());
        plugins.addAll(incap.getAllValues());
        plugins.addAll(outcap.getAllValues());
        plugins.addAll(qcap.getAllValues());

        Assert.assertTrue("no plugin registered " + pluginName, plugins.size() > 0);

        // cookbook is not like the others ;-)
        if (pluginName.equals("cookbook")) {
            return defaults;
        }

        List<Map<String, Object>> configs = new LinkedList<>();
        for (Plugin p : plugins) {
            Map<String, Object> cfg = new HashMap<>();
            p.writeConfig(mocke, cfg);
            if (cfg.size() > 0) {
                configs.add(cfg);
            }
        }
        Assert.assertFalse("multiple configs written " + pluginName, configs.size() > 1);
        Assert.assertFalse("no configs written " + pluginName, configs.size() == 0);

        Map<String, Object> config1 = configs.get(0);
        config1 = (Map<String, Object>) config1.values().iterator().next();
        return config1;
    }

    private Map<String, Object> constructDefault(Config config) {
        Map<String, Object> mo = new HashMap<>();

        for (Option o : config.getOptions()) {
            if (o.getDefaultv() == null || o.getDefaultv().isEmpty()) {
                mo.put(o.getName(), getDefaultFor(o));
            }
        }

        return mo;
    }

    private Object getDefaultFor(Option option) {
        return DefaultOverrides.getDefaultFor(pluginName, option);
    }

}
