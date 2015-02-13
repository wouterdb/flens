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

package flens.output;

import flens.core.Matcher;
import flens.core.Record;
import flens.output.util.AbstractPumpOutput;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;

public class FileOutput extends AbstractPumpOutput {

    private String field;
    private String file;
    private boolean newline;

    /**
     * @param name
     *            name under which this plugin is registered with the engine
     * @param plugin
     *            name of config that loaded this plugin (as registered in
     *            plugins.json)
     * @param matcher
     *            matcher this output should used to select records
     * @param file
     *            file to write to
     * @param field
     *            field to write to file
     * @param newline
     *            add newline after each record?
     */
    public FileOutput(String name, String plugin, Matcher matcher, String file, String field, boolean newline) {
        super(name, plugin, matcher);
        this.file = file;
        this.field = field;
        this.newline = newline;
    }

    @Override
    public void start() {

        super.start();
    }

    @Override
    public void run() {

        try (BufferedWriter os = new BufferedWriter(new FileWriter(file));) {

            while (running) {
                Record rin = queue.take();

                Object value = rin.getValues().get(field);
                if (value == null) {
                    lost++;
                    continue;
                    // TODO log?

                }

                os.write(value.toString());
                if (newline) {
                    os.write('\n');
                }
                os.flush();
                sent++;
            }
        } catch (UnknownHostException e) {

            err(getName() + " host not know", e);
        } catch (IOException e) {
            lost++;
            err(getName() + " pipe broken, going into reconnect", e);
            reconnect();
        } catch (InterruptedException e) {
            // normal
        }

    }

    @Override
    public void stop() {
        super.stop();

    }

}
