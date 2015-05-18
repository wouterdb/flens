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
