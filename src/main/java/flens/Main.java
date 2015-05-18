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
package flens;

import flens.core.ConfigBuilder;
import flens.core.Flengine;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {

    /**
     * Start new flens instance, from single config file, given as the first argument. 
     */
    public static void main(String[] args) throws FileNotFoundException {
        ConfigBuilder cb = new ConfigBuilder(new FileReader(args[0]));
        cb.run();
        Flengine fl = cb.getEngine();
        fl.start();
    }

}
