/*
 *
 *     Copyright 2013-2015 KU Leuven Research and Development - iMinds - Distrinet
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
package flens.typing;


import flens.util.GrokUtil;

import oi.thekraken.grok.api.Grok;
import oi.thekraken.grok.api.exception.GrokException;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LogTypesDb extends AbstractTypesDb<LogTypesDb> {

    private LogTypesDb() {

    }

    public LogTypesDb(String dir, boolean refresh) {
        if (refresh == true) {
            setupListener(dir);
        } else {
            loadDir(new File(dir));
        }

    }

    protected static final IOFileFilter files = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
            FileFilterUtils.suffixFileFilter(".db"));
    protected static final IOFileFilter filter = FileFilterUtils.or(FileFilterUtils.directoryFileFilter(), files);

    private List<LogType> types = new LinkedList<>();

    private Map<String, LogType> typesm = new HashMap<String, LogType>();

    public void parse(BufferedReader br, String filename) throws IOException, GrokException {

        Grok basegrok = GrokUtil.getGrok();
        LogType current = null;

        for (String line = br.readLine(); line != null; line = br.readLine()) {
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith("##")) {
                parseConfig(basegrok, line.substring(2));
            } else if (line.startsWith("#")) {
                continue;
            } else if (line.trim().startsWith("[")) {
                line = line.trim();
                line = line.replace("[", "").replace("]", "");
                current = new LogType(line);
                if (typesm.containsKey(line)) {
                    warn("duplicate section in types database: " + line + " " + filename);
                }
                add(current);

            } else {

                String[] parts = line.split("\\s+", 2);
                // name type grok
                if (parts.length != 2) {
                    warn("bad line in types from " + filename + ", wrong number of parts" + parts.length + ":" + line);
                    continue;
                }

                if (parts[0].equals("grok")) {
                    Grok ext = new Grok();
                    ext.copyPatterns(basegrok.getPatterns());
                    addGrok(current, parts[1], ext);
                } else if (parts[0].equals("filter")) {
                    addExpressions(current, parts[1]);
                } else if (parts[0].equals("mvel")) {
                    addScript(current, parts[1]);
                } else if (parts[0].equals("continue")) {
                    current.setContinue();
                } else {
                    warn("unknown line in types db: " + filename + " " + line);
                }

            }

        }

    }

    private void addScript(LogType current, String script) {
        current.setScript(script);

    }

    private void addExpressions(LogType current, String expression) {
        current.setExpression(expression);

    }

    private void addGrok(LogType current, String pattern, Grok ext) {
        try {
            current.setGrok(ext, pattern);
        } catch (GrokException e) {
            warn("bad grok pattern! " + pattern, e);
        }

    }

    private void parseConfig(Grok grok, String line) {
        if (line.startsWith("grok.file")) {
            try {
                grok.addPatternFromFile(line.substring(9).trim());
            } catch (Exception e) {
                warn("can not read file" + line.substring(9).trim(), e);
            }
        } else if (line.startsWith("grok.dir")) {

            String dir = line.substring(8).trim();
            if (dir != null && !dir.isEmpty()) {
                File dirh = new File(dir);
                if (!dirh.isDirectory()) {
                    warn("dir is not a directory " + dir);
                } else {
                    String[] files = dirh.list();
                    Arrays.sort(files);
                    for (String f : files) {
                        try {
                            grok.addPatternFromFile(dir + "/" + f);
                        } catch (Exception e) {
                            warn("can not read file" + f, e);
                        }

                    }
                }
            }

        } else {
            warn("invalid command: " + line);

        }

    }

    private void add(LogType metricType) {
        types.add(metricType);
        typesm.put(metricType.getName(), metricType);
    }

   /* private LogType get(String name) {
        LogType out = typesm.get(name);
        if (out == null) {
            out = new LogType(name);
            add(out);

        }
        return out;
    }*/

    public List<LogType> getAll() {
        return types;
    }

    @Override
    protected void addAll(LogTypesDb sub) {
        types.addAll(sub.getAll());
    }

    @Override
    protected void clear() {
        types.clear();
    }

    @Override
    protected LogTypesDb createSub() {
        return new LogTypesDb();
    }

}
