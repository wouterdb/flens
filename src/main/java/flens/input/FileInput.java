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

package flens.input;

import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractActiveInput;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

public class FileInput extends AbstractActiveInput {

    protected Pattern cregex;
    protected boolean tail;
    protected String file;
    protected String regex;

    /**
     * @param name
     *            name under which this plugin is registered with the engine
     * @param plugin
     *            name of config that loaded this plugin (as registered in
     *            plugins.json)
     * @param tagger
     *            tagger used to mark output records
     * @param file
     *            file to grep
     * @param regex
     *            regex to search for
     * @param tail
     *            tail the file or read once
     */
    public FileInput(String name, String plugin, Tagger tagger, String file, String regex, boolean tail) {
        super(name, plugin, tagger);
        this.file = file;
        this.regex = regex;
        this.cregex = Pattern.compile(regex);
        this.tail = tail;
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(file)))) {
           

            while (br.ready()) {
                handle(br.readLine());
            }
        } catch (IOException e) {
            err("could not read file!", e);
        }
    }

    public void handle(String line) {
        if (cregex.matcher(line).matches()) {
            dispatch(Record.forLog(file, line));
        }

    }

}
