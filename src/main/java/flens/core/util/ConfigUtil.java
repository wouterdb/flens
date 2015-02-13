/*
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

package flens.core.util;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ConfigUtil {

    /**
     * Collect all config from all .json files in the given directory;
     */
    public static Map<String, Object> collectConfig(String dir) throws JsonSyntaxException, JsonIOException,
            FileNotFoundException {
        List<Map<String, Object>> configs = new LinkedList<>();

        Gson gson = new Gson();

        File[] files = (new File(dir)).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("json");
            }
        });

        if (files == null || files.length == 0) {
            System.out.println("no config found");
            return new HashMap<String, Object>();
        }

        Arrays.sort(files);

        for (File f : files) {
            try {
                configs.add(gson.fromJson(new FileReader(f), HashMap.class));
            } catch (Exception e) {
                System.err.println("ignoring file: " + f.getAbsolutePath());
                e.printStackTrace();
            }
        }

        return merge(new HashMap<String, Object>(), configs);

    }

    private static Map<String, Object> merge(HashMap<String, Object> out, List<Map<String, Object>> configs) {
        for (Map<String, Object> map : configs) {
            merge((Map) out, (Map) map);
        }

        return out;
    }

  
    public static void merge(Map<Object, Object> out, Map<Object, Object> newMap) {
        for (Map.Entry entry : newMap.entrySet()) {
            if (!out.containsKey(entry.getKey())) {
                out.put(entry.getKey(), entry.getValue());
            } else {
                Object outSub = entry.getValue();
                Object newSub = out.get(entry.getKey());
                if (outSub instanceof Map) {
                    if (!(newSub instanceof Map)) {
                        System.out.println("type mismatch: discarding " + newSub);
                    } else {
                        merge((Map) newSub, (Map) outSub);
                    }
                } else if (outSub instanceof List) {
                    if (!(newSub instanceof List)) {
                        System.out.println("type mismatch: discarding " + newSub);
                    } else {
                        merge((List) newSub, (List) outSub);
                    }
                } else {
                    if (!newSub.equals(outSub)) {
                        System.out.println("non mergeable, ignoring " + newSub + " " + outSub);
                    }
                }
            }
        }

    }

    private static void merge(List outSub, List newSub) {
        outSub.addAll(newSub);
    }
}
