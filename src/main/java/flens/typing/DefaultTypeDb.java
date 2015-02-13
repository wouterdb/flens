package flens.typing;

import flens.util.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

public class DefaultTypeDb extends TypeDb {

    public DefaultTypeDb() {

        try (InputStream in = FileUtil.findFileOrResource(FileUtil.getSearchPathFor("types"), "typing.db")) {
            parse(new BufferedReader(new InputStreamReader(in)), "typing.db");
        } catch (FileNotFoundException e) {
            warn("no types database found", e);
        } catch (IOException e) {
            warn("failed to read types database", e);
        }
    }

    public DefaultTypeDb(String dir) {
        loadDir(new File(dir));
    }

    public DefaultTypeDb(String dir, boolean refresh) {
        if (refresh == true) {
            setupListener(dir);
        } else {
            loadDir(new File(dir));
        }
    }

}
