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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractTypesDb {

    protected static final IOFileFilter files = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
                FileFilterUtils.suffixFileFilter(".db"));
    protected static final IOFileFilter filter = FileFilterUtils.or(FileFilterUtils.directoryFileFilter(), files);
    
    public abstract void parse(BufferedReader br, String filename) throws Exception;
    
    public void load(File file) throws Exception {
        try (InputStream in = new FileInputStream(file)) {
            parse(new BufferedReader(new InputStreamReader(in)), file.getPath());
        }
    }

    public void loadDir(File directory) {
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
            } catch (Exception e) {
                warn("failed to read types database " + directory, e);
            } 
        }

        for (File f : FileUtils.listFiles(directory, files, TrueFileFilter.INSTANCE)) {
            try {
                load(f);
            } catch (FileNotFoundException e) {
                warn("file vanished before reading started " + f, e);
            } catch (Exception e) {
                warn("failed to read types database " + f, e);
            }
        }
    }

    protected void warn(String msg) {
        Logger.getLogger(getClass().getName()).warning(msg );
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
}
