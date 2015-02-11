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
import flens.input.util.AbstractPeriodicInput;

import org.mvel2.templates.CompiledTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TimerTask;

public class HttpPullInput extends AbstractPeriodicInput {

    protected URL myUrl;

    protected String[] collumnNames;
    protected CompiledTemplate[] collumnTemplates;

    protected String url;

    /**
     * @param name
     *            name under which this plugin is registered with the engine
     * @param plugin
     *            name of config that loaded this plugin (as registered in
     *            plugins.json)
     * @param tagger
     *            tagger used to mark output records
     * @param interval
     *            polling interval
     * @param url
     *            url to read 
     * @throws MalformedURLException
     *            url is bad
     */
    public HttpPullInput(String name, String plugin, Tagger tagger, int interval, String url)
        throws MalformedURLException {
        super(name, plugin, tagger, interval);
        this.url = url;
        this.myUrl = new URL(url);
    }

    protected void poll() throws IOException {
        HttpURLConnection con = (HttpURLConnection) myUrl.openConnection();

        // int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        Record out = Record.createWithMessage(response.toString());

        dispatch(out);

    }

    @Override
    protected TimerTask getWorker() {
        return new TimerTask() {

            @Override
            public void run() {
                try {
                    poll();
                } catch (IOException e) {
                    err("failed http pull", e);
                }
            }
        };
    }

}
