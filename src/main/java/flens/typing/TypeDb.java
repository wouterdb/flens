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

import static flens.util.ParseUtil.bool;
import static flens.util.ParseUtil.form;
import static flens.util.ParseUtil.list;
import static flens.util.ParseUtil.may;
import static flens.util.ParseUtil.nrHigh;
import static flens.util.ParseUtil.nrLow;
import static flens.util.ParseUtil.bool;
import static flens.util.ParseUtil.form;
import static flens.util.ParseUtil.list;
import static flens.util.ParseUtil.may;
import static flens.util.ParseUtil.nrHigh;
import static flens.util.ParseUtil.nrLow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import flens.util.FileUtil;

public class TypeDb {

    protected static final IOFileFilter files = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
            FileFilterUtils.suffixFileFilter(".db"));
    protected static final IOFileFilter filter = FileFilterUtils.or(FileFilterUtils.directoryFileFilter(), files);

    protected Map<String, MetricType> types = new HashMap<>();

    /**
     * @throws IllegalArgumentException
     *             when type collides with existing type
     */
    public synchronized void add(MetricType type) {
        if (types.containsKey(type.getName())) {
            MetricType other = types.get(type.getName());
            if (!other.equals(type)) {
                throw new IllegalArgumentException("types do not correspond: " + type + " " + other);
            }
        } else {
            types.put(type.getName(), type);
        }
    }

    public synchronized MetricType get(String metric) {
        return types.get(metric);
    }

    public void writeOut(PrintWriter pw) {
        for (MetricType type : types.values()) {
            // name resource unit form low high [int]
            String line = String.format("%s %s %s %s %s %s", type.getName(), type.getResource(), type.getUnit(), type
                    .getForm().toShortString(), type.getMinValue().toString(), type.getMaxValue().toString());
            pw.println(line);
        }
    }

    public void parse(BufferedReader br, String filename) throws IOException {
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            String[] parts = line.split("\\s+");
            // name resource unit form low high [int]
            if (parts.length != 6 && parts.length != 7) {
                warn("bad line in types from " + filename + ", wrong number of parts" + parts.length, line);
                continue;
            }
            if (parts.length == 6) {
                add(new MetricType(parts[0], parts[2], parts[1], form(parts[3]), nrLow(parts[4]), nrHigh(parts[5]),
                        true));
            } else {
                add(new MetricType(parts[0], parts[1], parts[2], form(parts[3]), nrLow(parts[4]), nrHigh(parts[5]),
                        bool(parts[7])));
            }

        }

    }

    public void load(File file) throws FileNotFoundException, IOException {
        try (InputStream in = new FileInputStream(file)) {
            parse(new BufferedReader(new InputStreamReader(in)), file.getPath());
        }
    }

    protected void loadDir(File directory) {
        if (!directory.exists()) {
            warn("file does not exist ", directory.toString());
            return;
        }
        if (directory.isFile()) {
            try {
                load(directory);
                return;
            } catch (FileNotFoundException e) {
                warn("file vanished before reading started " + directory, e);
            } catch (IOException e) {
                warn("failed to read types database " + directory, e);
            }
        }

        for (File f : FileUtils.listFiles(directory, files, TrueFileFilter.INSTANCE)) {
            try {
                load(f);
            } catch (FileNotFoundException e) {
                warn("file vanished before reading started " + f, e);
            } catch (IOException e) {
                warn("failed to read types database " + f, e);
            }
        }
    }

    protected void warn(String msg, String extra) {
        Logger.getLogger(getClass().getName()).warning(msg + " : " + extra);
    }

    protected void warn(String msg, Exception excn) {
        Logger.getLogger(getClass().getName()).log(Level.WARNING, msg, excn);

    }

    protected void info(String msg) {
        Logger.getLogger(getClass().getName()).info(msg);
    }

    public Collection<MetricType> getAll() {
        return types.values();
    }

}
