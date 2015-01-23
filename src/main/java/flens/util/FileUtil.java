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

package flens.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class FileUtil {

    /**
     * Find a file in one of several search paths. No subdir search.
     */
    public static File findFile(List<String> searchpath, String name) {
        for (String prefix : searchpath) {
            File file = new File(prefix, name);
            if (file.exists()) {
                return file;
            }
        }

        return null;
    }

    /**
     * /** Find a file in one of several search paths. No subdir search.
     */
    public static File findFile(String[] searchpath, String name) {
        for (String prefix : searchpath) {
            File file = new File(prefix, name);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }

    /**
     * Find a file in one of several search paths. No subdir search. Fallback to
     * classloader is no file is found.
     */
    public static InputStream findFileOrResource(String[] searchpath, String name) throws FileNotFoundException {
        for (String prefix : searchpath) {
            File file = new File(prefix, name);
            if (file.exists()) {
                return new FileInputStream(file);
            }
        }

        return FileUtil.class.getClassLoader().getResourceAsStream(name);

    }

    public static String[] getSearchPathFor(String service) {
        return new String[] { ".", "/usr/share/flens/" + service };
    }

}
