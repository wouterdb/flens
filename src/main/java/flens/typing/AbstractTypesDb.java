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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractTypesDb<T extends AbstractTypesDb<T>> implements FileAlterationListener {

    protected static final IOFileFilter files = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
            FileFilterUtils.suffixFileFilter(".db"));
    protected static final IOFileFilter filter = FileFilterUtils.or(FileFilterUtils.directoryFileFilter(), files);

    public abstract void parse(BufferedReader br, String filename) throws Exception;

    public void load(File file) throws Exception {
        try (InputStream in = new FileInputStream(file)) {
            try {
                parse(new BufferedReader(new InputStreamReader(in)), file.getPath());
            } catch (Exception e) {
                warn("file could not be loaded", e);
            }
        }
    }

    public void loadDir(File directory) {
        if (!directory.exists()) {
            warn("file does not exist ", directory.toString());
            return;
        }
        if (directory.isFile()) {
            try {
                onFileCreate(directory);
                return;
            } catch (Exception e) {
                warn("failed to read types database " + directory, e);
            }
        }

        for (File f : FileUtils.listFiles(directory, files, TrueFileFilter.INSTANCE)) {
            try {
                onFileCreate(f);
            } catch (Exception e) {
                warn("failed to read types database " + f, e);
            }
        }
    }

    protected void warn(String msg) {
        Logger.getLogger(getClass().getName()).warning(msg);
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

    // Updating behavior
    private Map<String, T> subs = new HashMap<String, T>();

    public void setupListener(String dir) {

        File directory = new File(dir);

        FileAlterationObserver observer = new FileAlterationObserver(directory, filter);

        observer.addListener(this);

        FileAlterationMonitor fam = new FileAlterationMonitor(100);
        fam.addObserver(observer);
        try {
            fam.start();
        } catch (Exception e) {
            throw new Error("should not occur", e);
        }
        loadDir(directory);

    }

    @Override
    public void onDirectoryCreate(File directory) {
    }

    @Override
    public void onDirectoryChange(File directory) {
    }

    @Override
    public void onDirectoryDelete(File directory) {
    }

    @Override
    public synchronized void onFileCreate(File file) {
        T sub = createSub();
        try {
            sub.load(file);
            subs.put(file.getPath(), sub);
            addAll(sub);
            info("add new file to types db: " + file);
        } catch (FileNotFoundException e) {
            warn("file vanished before reading started " + file, e);
        } catch (Exception e) {
            warn("could not read types from file", e);
        }

    }

    protected abstract void addAll(T sub);

    protected abstract void clear();

    protected abstract T createSub();

    private synchronized void rebuild() {
        clear();
        for (T sub : subs.values()) {
            addAll(sub);
        }
    }

    @Override
    public synchronized void onFileChange(File file) {
        T sub = createSub();
        try {
            sub.load(file);
            subs.put(file.getPath(), sub);
            rebuild();
            info("updated file for types db: " + file);
        } catch (FileNotFoundException e) {
            warn("file vanished before reading started " + file, e);
        } catch (Exception e) {
            warn("could not read types from file", e);
        }

    }

    @Override
    public synchronized void onFileDelete(File file) {
        subs.remove(file.getPath());
        rebuild();
        info("removed file from types db: " + file);
    }

    @Override
    public void onStop(FileAlterationObserver observer) {

    }

    @Override
    public void onStart(FileAlterationObserver observer) {
    }
}
