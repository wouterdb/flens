/**
 *
 *     Copyright 2013 KU Leuven Research and Development - iMinds - Distrinet
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     Administrative Contact: dnet-project-office@cs.kuleuven.be
 *     Technical Contact: wouter.deborger@cs.kuleuven.be
 */
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
import com.google.gson.internal.StringMap;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import dnet.monitor.control.FactsHandler;
import dnet.monitor.control.PingHandler;
import dnet.monitor.control.amqp.CommandHandler;
import dnet.monitor.control.amqp.CommandServer;
import flens.core.ConfigBuilder;
import flens.core.ConfigHandler;
import flens.core.Flengine;
import flens.core.GenericQueryTerm;
import flens.core.Util;

public class Service {

	public static void main(String[] args) throws IOException {

		ConfigHandler ch = new ConfigHandler();
		
		Map<String, Object> myconfig = collectConfig(args[0]);

		Map<String, String> tags = (Map<String, String>) myconfig.get("tags");

		Map<String, Object> initial = (Map<String, Object>) myconfig
				.get("init");

		String name = (String) myconfig.get("name");
		if(name != null)
			Util.overriderHostname(name);

		ch.load(initial);
		ch.getEngine().addTags(tags);
		ch.getEngine().start();
	}

	private static Map<String, Object> collectConfig(String dir)
			throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		List<Map<String, Object>> configs = new LinkedList<>();

		Gson g = new Gson();

		File[] files = (new File(dir)).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("json");
			}
		});

		Arrays.sort(files);

		for (File f : files) {
			try {
				configs.add(g.fromJson(new FileReader(f), HashMap.class));
			} catch (Exception e) {
				throw new JsonIOException("in file: " + f.getAbsolutePath(), e);
			}
		}

		return merge(new HashMap<String, Object>(), configs);

	}

	private static Map<String, Object> merge(HashMap<String, Object> out,
			List<Map<String, Object>> configs) {
		for (Map<String, Object> map : configs) {
			merge((Map) out, (Map) map);
		}

		return out;
	}

	private static void merge(Map<Object, Object> out,
			Map<Object, Object> newMap) {
		for (Map.Entry entry : newMap.entrySet()) {
			if (!out.containsKey(entry.getKey())) {
				out.put(entry.getKey(), entry.getValue());
			} else {
				Object outSub = entry.getValue();
				Object newSub = out.get(entry.getKey());
				if (outSub instanceof Map) {
					if (!(newSub instanceof Map)) {
						System.out.println("type mismatch: discarding "
								+ newSub);
					} else
						merge((Map) newSub, (Map) outSub);
				} else if (outSub instanceof List) {
					if (!(newSub instanceof List)) {
						System.out.println("type mismatch: discarding "
								+ newSub);
					} else
						merge((List) newSub, (List) outSub);
				} else {
					if (!newSub.equals(outSub))
						System.out.println("non mergeable, ignoring " + newSub
								+ " " + outSub);
				}
			}
		}

	}

	private static void merge(List outSub, List newSub) {
		outSub.addAll(newSub);
	}

}
