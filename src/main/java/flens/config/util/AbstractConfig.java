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

package flens.config.util;

import flens.core.Config;
import flens.core.Flengine;
import flens.core.Matcher;
import flens.core.Tagger;
import flens.core.util.AllMatcher;
import flens.core.util.InputTagger;
import flens.core.util.StandardMatcher;
import flens.core.util.StandardTagger;
import flens.core.util.TypeTagger;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class AbstractConfig implements Config {

    protected Map<String, Object> tree;
    protected Flengine engine;
    protected String name;
    protected String plugin;
    protected Matcher matcher;
    protected Tagger tagger;
    protected Logger logger = Logger.getLogger(getClass().getName());
    protected int prio = 100;

    @Override
    public void readConfigPart(String name, Map<String, Object> tree, Flengine engine) {
        this.tree = tree;
        this.engine = engine;
        this.name = name;
        this.plugin = get("plugin", name);

        logger.info("starting: " + name);

        if (isOut() || isFilter()) {
            matcher = readMatcher();
        }

        if (isIn() || isFilter()) {
            tagger = readTagger();
        }
        if (isFilter()) {
            checkLoopFree();
            prio = getInt("prio", 5);
        }
        construct();

        if (!tree.isEmpty()) {
            warn("unknown values {0}", tree);
        }
    }

    protected abstract boolean isIn();

    protected abstract boolean isOut();

    protected boolean isQuery() {
        return false;
    }

    protected boolean isFilter() {
        return !(isIn() || isOut() || isQuery());
    }

    protected abstract void construct();

    protected void warn(String string, Object... args) {
        logger.log(Level.WARNING, string, args);
    }

    protected void warn(String string, Throwable err) {
        logger.log(Level.WARNING, string, err);
    }

    protected void err(String string, Throwable err) {
        logger.log(Level.SEVERE, string, err);
    }

    private Tagger readTagger() {
        return readTagger("");

    }

    protected Tagger readTagger(String prefix) {

        List<String> tags = getArray(prefix + "add-tags", Collections.EMPTY_LIST);
        String stype = null;
        if (isIn()) {
            String type = get(prefix + "type", name);
            return new InputTagger(prefix, type, tags);
        } else {
            stype = get(prefix + "set-type", null);
        }

        rtags = getArray(prefix + "remove-tags", Collections.EMPTY_LIST);

        if (tags.isEmpty() && rtags.isEmpty()) {
            if (stype == null) {
                return Tagger.empty;
            } else {
                return new TypeTagger(prefix, stype);
            }
        }

        return new StandardTagger(prefix, stype, tags, rtags);

    }
    
    
    
    protected Matcher readMatcher() {
        tags = getArray("tags", Collections.EMPTY_LIST);
        String type = get("type", null);

        if (tags.isEmpty() && type == null) {
            return new AllMatcher();
        }

        return new StandardMatcher(type, tags);

    }

    /* *********************************************
     * looping
     */

    private List<String> tags;
    private List<?> rtags;
    private boolean loopfree;

    private void checkLoopFree() {
        for (String tag : tags) {
            if (rtags.contains(tag)) {
                loopfree = true;
            }
        }
    }

    protected void requiresLoopFree() {
        if (!loopfree) {
            warn("{0}: will cause infinite filterloop!, use the tags and remove-tag options to prevent this");
        }
    }

    /* ********************************************************
     * 
     * utilities******************************************************
     */

    protected List getArray(String name, List<?> defaultv) {
        Object object = tree.remove(name);
        if (object == null) {
            return defaultv;
        }
        if (object instanceof List) {
            return (List) object;
        }

        return Collections.singletonList(object);

        // throw new IllegalArgumentException("not a list: " + o);

    }

    protected String get(String name, String defaultv) {
        String res = (String) tree.remove(name);
        if (res == null) {
            return defaultv;
        }
        return res;
    }

    protected boolean getBool(String namex, boolean defaultv) {
        Object res = tree.remove(namex);

        if (res == null) {
            return defaultv;
        }
        if (res instanceof Boolean) {
            return ((Boolean) res).booleanValue();
        }

        return Boolean.parseBoolean((String) res);
    }

    protected int getInt(String namex, int defaultv) {
        Object res = tree.remove(namex);

        if (res == null) {
            return defaultv;
        }
        if (res instanceof Number) {
            return ((Number) res).intValue();
        }

        return Integer.parseInt((String) res);
    }

    /* **************************************
     * options*************************************
     */

    @Override
    public List<Option> getOptions() {

        if (isIn() && isOut()) {
            return loopopts;
        }

        if (isIn()) {
            return inopts;
        }

        if (isOut()) {
            return outopts;
        }
        if (isFilter()) {
            return filteropts;
        }

        return queryopts;
    }

    private static List<Option> inopts;
    private static List<Option> outopts;
    private static List<Option> filteropts;
    private static List<Option> loopopts;
    private static List<Option> queryopts;

    static {
        List<Option> matcherOpts = new LinkedList<Config.Option>();
        matcherOpts.add(new Option("tags", "[String]", "[]", "only apply to records having all of these tags"));
        matcherOpts.add(new Option("type", "String", "name", "only apply to records having this type"));
        
        List<Option> taggerOpts = new LinkedList<Config.Option>();
        taggerOpts.add(new Option("add-tags", "[String]", "[]", "add following tags"));

        Option name = new Option("plugin", "String", "plugin name", "name of the plugin used");
        
        inopts = new LinkedList<Option>();
        inopts.add(name);

        inopts.add(new Option("type", "String", "name", "type to apply to the records"));
        inopts.addAll(taggerOpts);

        filteropts = new LinkedList<Option>();
        filteropts.add(name);
        filteropts.addAll(taggerOpts);
        filteropts.addAll(matcherOpts);
        filteropts.add(new Option("prio", "int", "5", "execution priority"));
        filteropts.add(new Option("remove-tags", "[String]", "[]", "remove following tags"));
        filteropts.add(new Option("set-type", "String", "name", "only apply to records having this type"));

        outopts = new LinkedList<Option>();
        outopts.add(name);
        outopts.addAll(matcherOpts);

        queryopts = new LinkedList<>();
        queryopts.add(name);

        inopts = Collections.unmodifiableList(inopts);
        filteropts = Collections.unmodifiableList(filteropts);
        outopts = Collections.unmodifiableList(outopts);
        queryopts = Collections.unmodifiableList(queryopts);

        loopopts = new LinkedList<>();
        loopopts.add(name);
        loopopts.add(new Option("type", "String", "name", "type to apply to the records"));
        loopopts.addAll(taggerOpts);
        loopopts.addAll(matcherOpts);

    }
}
