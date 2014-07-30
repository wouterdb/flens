package flens;

import java.util.Collections;

import junit.framework.TestFailure;

import org.junit.Test;

import flens.core.Flengine;
import flens.core.PluginRepo;
import flens.core.util.InputTagger;
import flens.test.util.Pattern;
import flens.test.util.PatternInput;
import flens.test.util.PatternOutput;
import flens.test.util.PatternStore;

public class TestTest {

	@Test
	public void testPatternStore() throws InterruptedException{
		PatternInput pinp = new PatternInput("pinp", "pinp", new InputTagger("","test",Collections.<String>emptyList()));
		PatternOutput poutp = new PatternOutput("poutp", "poutp");
		
		load(pinp);
		load(poutp);
		
		Flengine testenFlengine = new Flengine(new PluginRepo());
		
		testenFlengine.addInput(pinp);
		testenFlengine.addOutput(poutp);
		
		testenFlengine.start();
		pinp.join();
		testenFlengine.stop();
		poutp.analyze(2.0f,0);
	}

	
	
	private void load(PatternStore st) {
		st.addPattern(new Pattern());
		st.addPattern(new Pattern(3000, 100, "test a "));
		st.addPattern(new Pattern(3000, 10, "test b "));
		st.addPattern(new Pattern(2000, 1, "test c "));
	}
}
