package flens.input.collectd;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.internal.verification.AtLeast;

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

@RunWith(Parameterized.class)
public class AllPluginRoundTest {

	static PluginRepo pr = new PluginRepo();
	private static Map<String,String> specialOverrides = new HashMap<>();
	
	
	static{
		specialOverrides.put("cookbook.template","dummy.tmpl");
	}
	
	@Parameters
	public static Iterable<Object[]> data() {
		List<Object[]> out = new LinkedList();
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
		Flengine mocke = mock(Flengine.class);
		when(mocke.getPluginRepo()).thenReturn(pr);
		plugin.readConfigPart(pluginName, defaults, mocke); 
		
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
