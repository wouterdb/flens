package flens.core;

import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class ConfigBuilder extends ConfigParser{
	
	
	
	
	private Map config;
	
	public ConfigBuilder(Reader is) {
		config = gson.fromJson(is, HashMap.class);
	}

	public void run() {
		construct(config);
	}

	
}
