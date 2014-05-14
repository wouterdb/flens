package flens.input;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import flens.config.Self;
import flens.config.util.Reflectable;
import flens.config.util.Reflector;
import flens.core.Config;
import flens.core.Flengine;
import flens.core.PluginRepo;
import static org.mockito.Mockito.*;

public class ReflectorTest {
	
	@Test
	public void relfectorTest1(){
		Flengine engine = mock(Flengine.class);
		when(engine.getPluginRepo()).thenReturn(new PluginRepo());
		
		Map<String,Object> conftree = new HashMap<>();
		
		Reflectable x = Reflector.contruct(engine, new SelfMonitor(), new Self(), "test", conftree);
		
		System.out.println(x);
	}

}
