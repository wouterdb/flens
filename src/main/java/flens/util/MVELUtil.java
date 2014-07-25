/**
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

import java.io.InputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import org.mvel2.ParserContext;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;

public class MVELUtil {

	private static ParserContext ctx;

	public static ParserContext getTooledContext(){
			return ctx;
	}
	
	static{
		ctx = new ParserContext();
		try {
		    ctx.addImport("reverseHostname", MVELUtil.class.getMethod("reverseHostname",String.class)); 
		    ctx.addImport("debug", MVELUtil.class.getMethod("debug"));
		}
		catch (NoSuchMethodException e) {
		    // handle exception here.
		}
	}
	
	public static String reverseHostname(String hostname){
		String[] parts = hostname.split("[.]");
		ArrayUtils.reverse(parts);
		return StringUtils.join(parts,".");
	}
	
	public static void debug(){
		System.out.println("debug");
	}
	
	public static CompiledTemplate compileTemplateTooled(String source){
		return TemplateCompiler.compileTemplate(source,getTooledContext());
	}
	
	public static CompiledTemplate compileTemplateTooled(InputStream source){
		return TemplateCompiler.compileTemplate(source,getTooledContext());
	}
}
