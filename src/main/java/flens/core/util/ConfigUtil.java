package flens.core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ConfigUtil {

	public static Map<String, Object> collectConfig(String dir)
			throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		List<Map<String, Object>> configs = new LinkedList<>();

		Gson g = new Gson();

		File[] files = (new File(dir)).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("json");
			}
		});

		if(files==null||files.length==0){
			System.out.println("no config found");
			return new HashMap<String, Object>();
		}
		
		Arrays.sort(files);

		for (File f : files) {
			try {
				configs.add(g.fromJson(new FileReader(f), HashMap.class));
			} catch (Exception e) {
				System.err.println("ignoring file: "+ f.getAbsolutePath());
				e.printStackTrace();
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
