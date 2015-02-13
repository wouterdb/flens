package flens.typing;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class UpdatingTypeDb extends TypeDb implements FileAlterationListener {

    private Map<String, TypeDb> subs = new HashMap<String, TypeDb>();

    public UpdatingTypeDb(String dir) {

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

    public void loadDir(File directory) {
        if (directory.isFile()) {
            onFileCreate(directory);
        } else {
            for (File f : FileUtils.listFiles(directory, files, TrueFileFilter.INSTANCE)) {
                onFileCreate(f);
            }
        }

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
        TypeDb sub = new TypeDb();
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

    private synchronized void rebuild() {
        types.clear();
        for (TypeDb sub : subs.values()) {
            addAll(sub);
        }
    }

    private void addAll(TypeDb sub) {
        for (MetricType m : sub.getAll()) {
            add(m);
        }
    }

    @Override
    public synchronized void onFileChange(File file) {
        TypeDb sub = new TypeDb();
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

    public static void main(String[] args) throws Exception {
        new UpdatingTypeDb("src/main/resources/");
    }

    @Override
    public void onStart(FileAlterationObserver observer) {
    }
}
