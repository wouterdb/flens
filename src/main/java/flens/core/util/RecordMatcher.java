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

package flens.core.util;

import flens.core.Matcher;
import flens.core.Record;
import flens.util.MvelUtil;

import org.mvel2.ConversionException;
import org.mvel2.DataConversion;
import org.mvel2.MVEL;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecordMatcher implements Matcher {

    private Set<String> tags;
    private String type;
    private String expression;
    private Serializable compiled;

    public RecordMatcher(String type, List<String> tags, String expression) {
        this.type = type;
        this.tags = new HashSet<String>(tags);
        this.expression = expression;

        try {
            this.compiled = MVEL.compileExpression(expression, MvelUtil.getTooledContext());

            testExpression();
        } catch (Exception e) {
            throw new IllegalArgumentException("filter expression failed for expression " + expression, e);

        }
    }

    private void testExpression() throws ConversionException {
        Record rec = Record.forLog("test");
        DataConversion.convert(MVEL.executeExpression(compiled, Collections.unmodifiableMap(rec.getValues())),
                Boolean.class);
    }

    @Override
    public boolean matches(Record rec) {
        if (type != null && !type.equals(rec.getType())) {
            return false;
        }
        if (!rec.getTags().containsAll(tags)) {
            return false;
        }

        try {
            boolean result = DataConversion.convert(
                    MVEL.executeExpression(compiled, Collections.unmodifiableMap(rec.getValues())), Boolean.class);
            return result;
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                    "matcher failed on expression: " + expression + " for record " + rec, e);
            return false;
        }

    }

    @Override
    public void outputConfig(Map<String, Object> tree) {
        tree.put("type", type);
        tree.put("tags", tags);
        tree.put("matches", expression);
    }
}
