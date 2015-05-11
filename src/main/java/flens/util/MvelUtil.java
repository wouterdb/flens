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

package flens.util;

import flens.typing.MetricType;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.mvel2.ParserContext;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;

import java.io.InputStream;

public class MvelUtil {

    private static ParserContext ctx;

    public static ParserContext getTooledContext() {
        return ctx;
    }

    private static ParserContext sctx;

    public static ParserContext getScriptingContext() {
        return sctx;
    }

    static {

        try {
            ctx = new ParserContext();
            ctx.addImport("reverseHostname", MvelUtil.class.getMethod("reverseHostname", String.class));
            ctx.addImport("debug", MvelUtil.class.getMethod("debug"));
            ctx.addImport("parseRange", MvelUtil.class.getMethod("parseRange", String.class));

            sctx = new ParserContext();

            sctx.addImport("reverseHostname", MvelUtil.class.getMethod("reverseHostname", String.class));
            sctx.addImport("debug", MvelUtil.class.getMethod("debug"));
            sctx.addImport("parseRange", MvelUtil.class.getMethod("parseRange", String.class));


        } catch (NoSuchMethodException e) {
            // handle exception here.
        }
    }

    /**
     * reverse a hostname, used for flattening host trees e.g. from
     * www.example.com to com.example.www
     */
    public static String reverseHostname(String hostname) {
        String[] parts = hostname.split("[.]");
        ArrayUtils.reverse(parts);
        return StringUtils.join(parts, ".");
    }

    /**
     * method for capturing breakpoints.
     */
    public static void debug() {
        System.out.println("debug");
    }

    /**
     * method for parsing range string.
     */
    public static Pair<Number, Number> parseRange(String range) {
        return MetricType.parseRange(range);
    }

    /**
     * method for capturing breakpoints.
     */
    public static void cleanGrokTime() {
        System.out.println("debug");
    }

    public static CompiledTemplate compileTemplateTooled(String source) {
        return TemplateCompiler.compileTemplate(source, getTooledContext());
    }

    public static CompiledTemplate compileTemplateTooled(InputStream source) {
        return TemplateCompiler.compileTemplate(source, getTooledContext());
    }

}
