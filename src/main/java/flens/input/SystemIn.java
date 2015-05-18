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
package flens.input;

import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractActiveInput;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SystemIn extends AbstractActiveInput {

    private BufferedReader inr;

    public SystemIn(String name, String plugin, Tagger tagger) {
        super(name, plugin, tagger);
    }

    public void start() {
        this.inr = new BufferedReader(new InputStreamReader(System.in));
        super.start();
    }

    @Override
    public void stop() {
        super.stop();

        try {
            inr.close();
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "cloud not close", e);
        }

    }

    @Override
    public void run() {
        try {
            while (running) {
                String line;

                line = inr.readLine();

                dispatch(Record.forLog(line));
            }

        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "system in failed", e);
        }
    }

}
