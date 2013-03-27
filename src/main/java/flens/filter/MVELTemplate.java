package flens.filter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mvel2.CompileException;
import org.mvel2.MVEL;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

public class MVELTemplate extends AbstractFilter{

	private static final Logger log = Logger.getLogger(MVELTemplate.class.getName()); 
	
	private String script;
	private CompiledTemplate compiled;
	private String field;


	public MVELTemplate(String name, Tagger tagger, Matcher matcher,String field,String script) {
		super(name, tagger, matcher);
		this.field = field;
		this.script = script;
		start();
	}
	
	

	private void start() {
		 // Compile the expression.
		compiled = TemplateCompiler.compileTemplate(script);
	}



	@Override
	public Collection<Record> process(Record in) {
		try{
		
		String out = (String) TemplateRuntime.execute(compiled, in.getValues());
		in.getValues().put(field,out);
		}catch(CompileException e){
			log.log(Level.SEVERE, "MVEL failed, context: " + in.getValues(), e);
		}
		return Collections.EMPTY_LIST;
		
		/*if(records == null)
			return Collections.EMPTY_LIST;
		if(records instanceof Record)
			return Collections.singletonList((Record)records);
		if(records instanceof Collection){
			Collection c = (Collection)records;
			if(c.isEmpty())
				return Collections.EMPTY_LIST;
			Object f = c.iterator().next();
			if(!(f instanceof Record)){
				warn("mvel returned wrong type in list "+f.getClass().getName() + " should be list of records or record");
				return Collections.EMPTY_LIST;
			}
			return c;
		}
		
		warn("mvel returned wrong type "+records.getClass().getName() + " should be list of records or record");
		return Collections.EMPTY_LIST;*/
	}

}
