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

package flens.filter;

import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

import com.nflabs.grok.Grok;
import com.nflabs.grok.GrokException;
import com.nflabs.grok.Match;

import java.io.File;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;

public class GrokFilter extends AbstractFilter {

    private String script;
    private Grok compiled;
    private String field;
    private String dir;
    private boolean discard;

    /**
     * Filter that unpack strings into fields using Grok 
     * 
     * @param name
     *            name under which this plugin is registered with the engine
     * @param plugin
     *            name of config that loaded this plugin (as registered in
     *            plugins.json)
     * @param tagger 
     *            tagger used to mark output records
     * @param matcher
     *            matcher this filter should used to select recrods
     * @param prio
     *            plugin priority
     * @param script
     *            grok pattern
     * @param inField
     *            field to apply pattern to
     * @param dir
     *            directory with additional grok patterns
     * @param discardNonMatches
     *            discard all non matching records
     */
    public GrokFilter(String name, String plugin, Tagger tagger, Matcher matcher, int prio, String script,
            String inField, String dir, boolean discardNonMatches) {
        super(name, plugin, tagger, matcher, prio);
        this.script = script;
        this.field = inField;
        this.dir = dir;
        this.discard = discardNonMatches;
        start();
    }

    private void start() {
        // Compile the expression.
        compiled = new Grok();
        try {
            compiled.addPatternFromReader(new InputStreamReader(GrokFilter.class.getResourceAsStream("/grok/base")));

            compiled.addPatternFromReader(new InputStreamReader(GrokFilter.class.getResourceAsStream("/grok/java")));
        } catch (Throwable e) {
            throw new IllegalArgumentException("could not load aux patterns", e);
        }
        if (dir != null && !dir.isEmpty()) {
            File dirh = new File(dir);
            if (!dirh.isDirectory()) {
                warn("dir is not a directory" + dir);
            } else {
                for (String f : dirh.list()) {

                    try {
                        compiled.addPatternFromFile(f);
                    } catch (Throwable e) {
                        err("can not read file" + f, e);
                    }

                }
            }
        }

        try {
            compiled.compile(script);
        } catch (GrokException e) {
            throw new IllegalArgumentException("bad grok pattern", e);
        }

    }

    @Override
    public Collection<Record> process(Record in) {
        String inf = (String) in.getValues().get(field);
        if (inf == null) {
            return Collections.emptyList();
        }
        Match match = compiled.match(inf);
        if (match == null || match.isNull()) {
            if (discard) {
                in.setType(null);
            }
            return Collections.emptyList();
        }

        match.captures();

        in.getValues().putAll(match.toMap());
        tag(in);
        return Collections.emptyList();
    }

}
