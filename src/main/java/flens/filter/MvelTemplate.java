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
package flens.filter;

import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;
import flens.util.MvelUtil;

import org.mvel2.CompileException;
import org.mvel2.UnresolveablePropertyException;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MvelTemplate extends AbstractFilter {

    private static final Logger log = Logger.getLogger(MvelTemplate.class.getName());

    protected String[] collumnNames;
    protected CompiledTemplate[] collumnTemplates;

    protected List<String> field;
    protected List<String> template;

    /**
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
     * @param field
     *            fields to store results in
     * @param collumnTemplates
     *            mvel templates to set the fields
     */
    public MvelTemplate(String name, String plugin, Tagger tagger, Matcher matcher, int prio, List<String> field,
            List<String> collumnTemplates) {
        super(name, plugin, tagger, matcher, prio);
        this.field = field;
        this.template = collumnTemplates;
        this.collumnNames = field.toArray(new String[0]);
        this.collumnTemplates = new CompiledTemplate[collumnTemplates.size()];
        for (int i = 0; i < collumnTemplates.size(); i++) {
            this.collumnTemplates[i] = MvelUtil.compileTemplateTooled(collumnTemplates.get(i));
        }
    }

    @Override
    public Collection<Record> process(Record in) {

        try {
            Object[] out = new Object[this.collumnTemplates.length];
            for (int i = 0; i < this.collumnTemplates.length; i++) {
                out[i] = TemplateRuntime.execute(this.collumnTemplates[i], in.getValues());
            }

            for (int i = 0; i < out.length; i++) {
                in.getValues().put(collumnNames[i], out[i]);
            }

            tag(in);
        } catch (CompileException | UnresolveablePropertyException e) {
            log.log(Level.SEVERE, "MVEL failed, context: " + in.getValues(), e);
        }
        return Collections.emptyList();

        /*
         * if(records == null) return Collections.EMPTY_LIST; if(records
         * instanceof Record) return Collections.singletonList((Record)records);
         * if(records instanceof Collection){ Collection c =
         * (Collection)records; if(c.isEmpty()) return Collections.EMPTY_LIST;
         * Object f = c.iterator().next(); if(!(f instanceof Record)){
         * warn("mvel returned wrong type in list "+f.getClass().getName() +
         * " should be list of records or record"); return
         * Collections.EMPTY_LIST; } return c; }
         * 
         * warn("mvel returned wrong type "+records.getClass().getName() +
         * " should be list of records or record"); return
         * Collections.EMPTY_LIST;
         */
    }

}
