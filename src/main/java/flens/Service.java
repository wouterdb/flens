package flens;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import dnet.monitor.control.PingHandler;
import dnet.monitor.control.amqp.CommandHandler;
import dnet.monitor.control.amqp.CommandServer;

import flens.core.ConfigBuilder;
import flens.core.ConfigHandler;
import flens.core.Flengine;

public class Service {

	public static void main(String[] args) throws IOException {

		ConfigHandler ch = new ConfigHandler();

		

		Map<String, Object> myconfig = collectConfig(args[0]);

		Map<String, String> tags = (Map<String, String>) myconfig.get("tags");
		
		Map<String, Object> initial = (Map<String, Object>) myconfig.get("init");

		String server = (String) myconfig.get("server");
		String name = (String) myconfig.get("name");
		
		if(server==null || name==null){
			System.out.println("no config,....");
			System.exit(-1);
		}

		System.out.println(String.format("connecting to %s as %s, with tags %s",server,name,tags));
		
		List<CommandHandler> chs = new LinkedList<>();
		chs.add(new PingHandler());
		chs.add(ch);

		ConnectionFactory c = new ConnectionFactory();
		c.setHost(server);
		c.setUsername("guest");
		c.setPassword("guest");
		c.setPort(5672);

		CommandServer cs = new CommandServer(name, c.newConnection(), tags, chs);
		cs.start();
		ch.load(initial);
		ch.getEngine().addTags(tags);
		ch.getEngine().start();
	}

	private static Map<String, Object> collectConfig(String dir) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		List<Map<String, Object>> configs = new LinkedList<>();
		
		Gson g = new Gson();
		
		File[] files = (new File(dir)).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("json");
			}
		});
		
		Arrays.sort(files);
		
		for(File f:files){
			configs.add(g.fromJson(new FileReader(f),
					HashMap.class));
		}
		
		return merge(new HashMap<String, Object>(), configs);
		
	}

	private static Map<String, Object> merge(HashMap<String, Object> out,
			List<Map<String, Object>> configs) {
		for (Map<String, Object> map : configs) {
			merge((Map)out,(Map)map);
		}
		
		return out;
	}

	private static void merge(Map<Object,Object> out,
			Map<Object,Object> newMap) {
		for (Map.Entry entry : newMap.entrySet()) {
			if(!out.containsKey(entry.getKey())){
				out.put(entry.getKey(), entry.getValue());
			}else{
				Object outSub = entry.getValue();
				Object newSub = out.get(entry.getKey());
				if(outSub instanceof Map){
					if(!(newSub instanceof Map)){
						System.out.println("type mismatch: discarding " + newSub);
					}else
						merge((Map)outSub,(Map)newSub);
				}else if (outSub instanceof List){
					if(!(newSub instanceof List)){
						System.out.println("type mismatch: discarding " + newSub);
					}else
						merge((List)outSub,(List)newSub);
				}else{
						if(!newSub.equals(outSub))
							System.out.println("non mergeable, ignoring " + newSub +" "+ outSub);
				}
			}
		}
		
	}

	private static void merge(List outSub, List newSub) {
		outSub.addAll(newSub);
	}

	
}
