package flens.core;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class PluginRepo {

	private Gson gson = new Gson();
	private Map<String,String> raw ;
	private Map<String,Config> processed = new HashMap<String, Config>() ;
	
	public PluginRepo(InputStream in) {
		this.raw   = gson.fromJson(new InputStreamReader(in), HashMap.class));
	}

	

	
	public Config get(String key) {
		if(processed.containsKey(key)){
			return processed.get(key);
		}
		String clazz = raw.get(key);
		
		
		Config x = (Config) getClass().getClassLoader().loadClass(clazz).newInstance(); 
		
		processed.put(key, x);
		
		return x;
		
	}

}
