package flens;

import java.io.FileNotFoundException;
import java.io.FileReader;

import flens.core.ConfigBuilder;
import flens.core.ConfigParser;
import flens.core.Flengine;

public class Help {
	
	public static void main(String[] args) throws FileNotFoundException {
		ConfigParser cp = new ConfigParser();
		System.out.println(cp.help());
	}

}
