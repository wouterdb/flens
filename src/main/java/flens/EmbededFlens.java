package flens;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import org.elasticsearch.common.mvel2.ast.Instance;

import flens.core.ConfigBuilder;
import flens.core.Flengine;

public class EmbededFlens {
	
	
	
	private Flengine engine;

	public EmbededFlens() {
		
		Reader s = getConfig();
		if(s==null)
			return;
		ConfigBuilder cb = new ConfigBuilder(s);
		cb.run();
		this.engine=cb.getEngine();
	}

	private Reader getConfig() {
		String prop = System.getProperty("flens.config");
		if(prop==null)
			return null;
		try {
			return new FileReader(prop);
		} catch (FileNotFoundException e) {
			//fixme: log decently
			e.printStackTrace();
			return null;
		}
	}
	
	public Flengine getEngine() {
		return engine;
	}
	
	
	private static EmbededFlens instance;
	
	public static EmbededFlens getInstance(){
		if(instance != null)
			return instance;
		synchronized(EmbededFlens.class){
			if(instance != null)
				return instance;
			instance = new EmbededFlens();
			if(instance.engine!=null)
				instance.engine.start();
		}
		return instance;
	}

}
