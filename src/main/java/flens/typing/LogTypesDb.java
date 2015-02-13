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

package flens.typing;

import static flens.util.ParseUtil.may;

import flens.util.GrokUtil;

import oi.thekraken.grok.api.Grok;
import oi.thekraken.grok.api.exception.GrokException;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;



public class LogTypesDb extends AbstractTypesDb {
    
    public LogTypesDb(String dir) {
        loadDir(new File(dir));
    }
    
    protected static final IOFileFilter files = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
            FileFilterUtils.suffixFileFilter(".db"));
    protected static final IOFileFilter filter = FileFilterUtils.or(FileFilterUtils.directoryFileFilter(), files);

    private List<LogType> types = new LinkedList<>();

    public void parse(BufferedReader br, String filename) throws IOException, GrokException {

        Grok basegrok = GrokUtil.getGrok();
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            if (line.startsWith("##")) {
                parseConfig(basegrok, line.substring(2));
            } else if (line.startsWith("#")) {
                continue;
            } else {

                String[] parts = line.split("\\s+", 3);
                // name type grok
                if (parts.length != 3) {
                    warn("bad line in types from " + filename + ", wrong number of parts" + parts.length + ":" + line);
                    continue;
                }
                Grok ext = new Grok();
                ext.copyPatterns(basegrok.getPatterns());
                add(new LogType(parts[0], may(parts[1]), parts[2], ext));

            }

        }

    }

    private void parseConfig(Grok grok, String line) {
        if (line.startsWith("grok.dir")) {

            String dir = line.substring(8);
            System.out.println("Dir:" + dir);
            if (dir != null && !dir.isEmpty()) {
                File dirh = new File(dir);
                if (!dirh.isDirectory()) {
                    warn("dir is not a directory " + dir);
                } else {
                    for (String f : dirh.list()) {
                        try {
                            grok.addPatternFromFile(f);
                        } catch (Exception e) {
                            warn("can not read file" + f, e);
                        }

                    }
                }
            }

        }

    }

    private void add(LogType metricType) {
        types.add(metricType);
    }

    public List<LogType> getAll() {
        return types;
    }

  

}
