package flens;

import java.io.FileNotFoundException;
import java.io.FileReader;

import flens.core.ConfigBuilder;
import flens.core.Flengine;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException {
		ConfigBuilder cb = new ConfigBuilder(new FileReader(args[0]));
		cb.run();
		Flengine fl = cb.getEngine();
		fl.start();
	}

}
