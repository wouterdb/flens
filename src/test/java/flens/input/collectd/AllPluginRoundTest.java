package flens.input.collectd;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.ArgumentCaptor;

import flens.core.Config;
import flens.core.Config.Option;
import flens.core.Filter;
import flens.core.Flengine;
import flens.core.Input;
import flens.core.Output;
import flens.core.Plugin;
import flens.core.PluginRepo;
import flens.core.QueryHandler;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;


@RunWith(Parameterized.class)
public class AllPluginRoundTest {

	static PluginRepo pr = new PluginRepo();
	private static Map<String,String> specialOverrides = new HashMap<>();
	
	
	static{
		specialOverrides.put("cookbook.template","dummy.tmpl");
		specialOverrides.put("grep.file","/etc/hosts");
		specialOverrides.put("http-poll.url","http://www.google.be/");
	}
	
	@Parameters(name="{index}: {0}")
	public static Iterable<Object[]> data() {
		List<Object[]> out = new LinkedList<>();
		for(String name:pr.names())
			out.add(new Object[]{name});
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
		Map<String,Object> defaults = constructDefault(plugin);
		
		
		Map<String, Object> config1 = configRoundTrip(defaults);
		org.junit.Assert.assertFalse(config1.size()==0);
		
		if(config1.equals(defaults))
			return;
		Map<String, Object> config2 = configRoundTrip(config1);
		assertThat(config2, equalTo(config1));
	
		
		Map<String, Object> config3 = configRoundTrip(config2);
		assertThat(config3, equalTo(config2));
		
		System.out.println(config3);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> configRoundTrip(Map<String, Object> defaults) {
		Flengine mocke = mock(Flengine.class);
		when(mocke.getPluginRepo()).thenReturn(pr);
		plugin.readConfigPart(pluginName, new HashMap<>(defaults), mocke); 
		
		ArgumentCaptor<Filter> filtercap = ArgumentCaptor.forClass(Filter.class);
		verify(mocke,atLeast(0)).addFilter(filtercap.capture());
		
		ArgumentCaptor<Input> incap = ArgumentCaptor.forClass(Input.class);
		verify(mocke,atLeast(0)).addInput(incap.capture());
		
		ArgumentCaptor<Output> outcap = ArgumentCaptor.forClass(Output.class);
		verify(mocke,atLeast(0)).addOutput(outcap.capture());
		
		
		ArgumentCaptor<QueryHandler> qcap = ArgumentCaptor.forClass(QueryHandler.class);
		verify(mocke,atLeast(0)).addHandler(qcap.capture());
		
		List<Plugin> plugins = new LinkedList<Plugin>();
		plugins.addAll(filtercap.getAllValues());
		plugins.addAll(incap.getAllValues());
		plugins.addAll(outcap.getAllValues());
		plugins.addAll(qcap.getAllValues());
		
		Assert.assertTrue("no plugin registered " + pluginName,plugins.size()>0);
		
		//cookbook is not like the others ;-)
		if(pluginName.equals("cookbook"))
			return defaults;
		
		List<Map<String,Object>> configs = new LinkedList<>();
		for (Plugin p : plugins) {
			Map<String,Object> cfg = new HashMap<>(); 
			p.writeConfig(mocke, cfg);
			if(cfg.size()>0)
				configs.add(cfg);
		}
		Assert.assertFalse("multiple configs written " + pluginName,configs.size()>1);
		Assert.assertFalse("no configs written " + pluginName,configs.size()==0);
		
		Map<String,Object> config1 = configs.get(0);
		config1 = (Map<String, Object>) config1.values().iterator().next();
		return config1;
	}

	private Map<String, Object> constructDefault(Config config) {
		Map<String, Object> mo = new HashMap<>();
		
		for(Option o:config.getOptions()){
			if(o.getDefaultv()==null || o.getDefaultv().isEmpty()){
				mo.put(o.getName(),getDefaultFor(o));
			}
		}
		
		return mo;
	}

	private Object getDefaultFor(Option o) {
		if(specialOverrides.containsKey(pluginName+"."+o.getName()))
			return specialOverrides.get(pluginName+"."+o.getName());
		return getDefaultFor(o.getType());
	}

	private Object getDefaultFor(String type) {
		if("String".equals(type))
			return "UNKNOWN";
		throw new IllegalArgumentException(type);
	}

}
