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

package flens.typing;

import flens.core.Constants;
import flens.core.Record;
import flens.typing.scripting.ElasticSearchUtil;
import flens.typing.scripting.GrokUtil;
import flens.typing.scripting.StatsdUtil;
import flens.util.MvelUtil;

import oi.thekraken.grok.api.Grok;
import oi.thekraken.grok.api.Match;
import oi.thekraken.grok.api.exception.GrokException;

import org.mvel2.DataConversion;
import org.mvel2.MVEL;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogType {

    private String name;
    private String pattern;
    private Grok grok;

    private String expression;
    private Serializable compiledexpression;

    private String script;
    private Serializable compiledscript;

    private boolean continueafter = false;

    public LogType(String name) {
        super();
        this.name = name;
    }

    public void setGrok(Grok grok, String pattern) throws GrokException {
        this.pattern = pattern;
        this.grok = grok;
        grok.compile(pattern);
    }

    public void setExpression(String expression) {
        this.expression = expression;

        try {
            this.compiledexpression = MVEL.compileExpression(expression, MvelUtil.getTooledContext());
        } catch (Exception e) {
            throw new IllegalArgumentException("filter expression failed for expression " + expression, e);

        }
    }

    public void setScript(String script) {
        this.script = script;

        try {
            this.compiledscript = MVEL.compileExpression(script, MvelUtil.getScriptingContext());
        } catch (Exception e) {
            throw new IllegalArgumentException("script failed: " + expression, e);

        }

    }

    public void setContinue() {
        continueafter = true;
    }

    public String getName() {
        return name;
    }

    public String getPattern() {
        return pattern;
    }

    public LogMatch match(Record rec) {
        boolean expmatch = false;
        if (compiledexpression != null) {
            try {
                expmatch = DataConversion.convert(
                        MVEL.executeExpression(compiledexpression, Collections.unmodifiableMap(rec.getValues())),
                        Boolean.class);

            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.FINE,
                        "log type expression failed: " + expression + " for record " + rec, e);
            }
            // return on failure, or on false
            if (!expmatch) {
                return null;
            }
        }

        Map<String, Object> out = rec.getValues();
        Set<String> tags = Collections.emptySet();

        if (grok != null) {
            Match adding = grok.match((String) rec.get(Constants.MESSAGE));
            adding.captures();
            if (!adding.isNull()) {
                out.putAll(adding.toMap());
            } else {
                return null;
            }
        }

        if (script != null) {
            tags = new HashSet<>();
            out.put("_grok",  new GrokUtil(out, tags));
            out.put("_statsd", new StatsdUtil(out, tags));
            out.put("_es", new ElasticSearchUtil(out, tags));
            out.put("_", new flens.typing.scripting.MvelUtil(out,tags));
            
            try {
                MVEL.executeExpression(compiledscript, out);
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "script failure", e);
            }

            out.remove("_grok");
            out.remove("_statsd");
            out.remove("_es");
            out.remove("_");
        }
        return new LogMatch(tags, this, continueafter);

    }

}
