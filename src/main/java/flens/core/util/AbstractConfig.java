package flens.core.util;

import java.io.ObjectInputStream.GetField;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import flens.core.Config;
import flens.core.Flengine;
import flens.core.Matcher;
import flens.core.Tagger;

public abstract class AbstractConfig implements Config {

	protected Map<String, Object> tree;
	protected Flengine engine;
	protected String name;
	protected Matcher matcher;
	protected Tagger tagger;
	protected Logger logger = Logger.getLogger(getClass().getName());
	protected int prio = 100;
	
	@Override
	public void readConfigPart(String name, Map<String, Object> tree, Flengine engine) {
		this.tree = tree;
		this.engine = engine;
		this.name = name;
		
		logger.info("starting: "+name);
		
		if(!isIn())
			matcher = readMatcher();
		
		if(!isOut())
			tagger = readTagger();

		if(isFilter()){
			checkLoopFree();
			prio = getInt("prio", 5);
		}
		construct();
		
		if(!tree.isEmpty())
			warn("unknown values {0}", tree);
	}

	protected abstract boolean isIn();
	protected abstract boolean isOut();
	
	protected boolean isQuery(){
		return false;
	}
	
	protected boolean isFilter() {
		return !(isIn()||isOut()||isQuery());
	}

	protected abstract void construct();

	protected void warn(String string, Object ...args) {
		logger.log(Level.WARNING,string,args);
	}

	
	private Tagger readTagger() {
		return readTagger("");
	
	}

	protected Tagger readTagger(String prefix) {
		List tags = getArray(prefix+"add-tag",Collections.EMPTY_LIST);
		String stype = null;
		if(isIn()){
			String type = get(prefix+"type",name);
			return new InputTagger(type,tags);
		}else{
			stype = get(prefix+"set-type",null);
		}
		
		rtags = getArray(prefix+"remove-tag",Collections.EMPTY_LIST);
		
		if(tags.isEmpty() && rtags.isEmpty()){
			if(stype == null)
				return Tagger.empty;
			else
				return new TypeTagger(stype);
		}
			
		return new StandardTagger(stype,tags,rtags);
	
	}
	
	private Matcher readMatcher() {
		tags = getArray("tags",Collections.EMPTY_LIST);
		String type = get("type",null);
		
		if(tags.isEmpty() && type == null)
			return new AllMatcher();
		
		return new StandardMatcher(type,tags);
		
		
	}
	
	/**********************************************
	 * looping
	 **********************************************/
	
	private List<String> tags;
	private List rtags;
	private boolean loopfree;
	

	private void checkLoopFree(){
		for (String tag : tags) {
			if(rtags.contains(tag))
				loopfree = true;
		}
	}
	
	protected void requiresLoopFree() {
		if(!loopfree)
			warn("{0}: will cause infinite filterloop!, use the tags and remove-tag options to prevent this");
	}

	
	/*********************************************************
	 * 
	 * utilities
	 ********************************************************/
	
	protected List getArray(String name, List defaultv) {
		Object o = tree.remove(name);
		if(o == null)
			return defaultv;
		
		if(o instanceof List)
			return (List) o;
		
		
			return Collections.singletonList(o);
		
		//throw new IllegalArgumentException("not a list: " + o);
		
	}

	protected String get(String name, String defaultv) {
		String res = (String) tree.remove(name);
		if(res == null)
			return defaultv;
		return res;
	}
	
	
	protected boolean getBool(String namex, boolean defaultv) {
		Object res = tree.remove(namex);
		
		if(res == null)
			return defaultv;
		if(res instanceof Boolean)
			return ((Boolean)res).booleanValue();
	
		
		return Boolean.parseBoolean((String) res);
	}

	
	protected int getInt(String namex, int defaultv) {
		Object res = tree.remove(namex);
		
		if(res == null)
			return defaultv;
		if(res instanceof Number)
			return ((Number)res).intValue();
	
		
		return Integer.parseInt( (String) res);
	}


	/***************************************
	 * options
	 ***************************************/
	
	@Override
	public List<Option> getOptions() {
		
		if(isIn())
			return inopts;
		
		if(isOut())
			return outopts;
		
		return filteropts;
	}
	
	private static List<Option> inopts; 
	private static List<Option> outopts;
	private static List<Option> filteropts;
	
	static{
		Option name =new Option("name", "String", "plugin name" ,"name of the filter, for reporting and monitoring purposes"); 
		
		List<Option> matcherOpts = new LinkedList<Config.Option>();
		matcherOpts.add(new Option("type", "String","name", "only apply to records having this type"));
		matcherOpts.add(new Option("tags", "[String]","[]", "only apply to records having all of these tags"));
		
		List<Option> taggerOpts = new LinkedList<Config.Option>();
		matcherOpts.add(new Option("add-tags", "[String]","[]", "add following tags"));
		matcherOpts.add(new Option("remove-tags", "[String]","[]", "remove following tags"));
		
		
		inopts = new LinkedList<Option>();
		inopts.add(name);
		inopts.add(new Option("type", "String", "name" ,"type to apply to the records"));
		inopts.addAll(taggerOpts);
		
		filteropts = new LinkedList<Option>();
		filteropts.add(name);
		filteropts.addAll(taggerOpts);
		filteropts.addAll(matcherOpts);
		filteropts.add(new Option("prio", "int", "5", "execution priority")); 
				
		
		outopts = new LinkedList<Option>();
		outopts.add(name);
		outopts.addAll(matcherOpts);
		
		
		inopts = Collections.unmodifiableList(inopts);
		filteropts = Collections.unmodifiableList(filteropts);
		outopts = Collections.unmodifiableList(outopts);
		
	}
}
