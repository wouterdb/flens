package flens.test;

import java.io.FileNotFoundException;
import java.io.FileReader;

import flens.core.ConfigBuilder;
import flens.core.Flengine;

public class ConfigTest {
	
	public static void main(String[] args) throws FileNotFoundException {
		ConfigBuilder cb = new ConfigBuilder(new FileReader("config.json"));
		cb.run();
		Flengine fl = cb.getEngine();
		
		fl.start();
		
		
		
	}

}
