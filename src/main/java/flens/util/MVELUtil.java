package flens.util;

import java.io.Serializable;

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
	
	public static CompiledTemplate compileTemplateTooled(String source){
		return TemplateCompiler.compileTemplate(source,getTooledContext());
	}
}
