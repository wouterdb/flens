package flens.core;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

public class PluginRepo {

	private Gson gson = new Gson();
	private Map<String,String> raw ;
	private Map<String,Config> processed = new HashMap<String, Config>() ;
	
	public PluginRepo(InputStream in) {
		this.raw   = gson.fromJson(new InputStreamReader(in), HashMap.class);
	}

	

	
	public Config get(String key) {
		if(processed.containsKey(key)){
			return processed.get(key);
		}
		String clazz = raw.get(key);
		
		
		Config x;
		try {
			x = (Config) getClass().getClassLoader().loadClass(clazz).newInstance();
			processed.put(key, x);
			
			return x;
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.INFO,"plugin not found",e);
			return null;
		} 
		
		
	}

}
