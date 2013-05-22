package flens.core;

import java.io.ObjectInputStream.GetField;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import flens.core.util.StandardTagger;

public abstract class AbstractConfig implements Config {

	protected Map<String, Object> tree;
	protected Flengine engine;
	protected String name;
	protected Matcher matcher;
	protected Tagger tagger;
	protected Logger logger = Logger.getLogger(getClass().getName());
	
	@Override
	public void readConfigPart(String name, Map<String, Object> tree, Flengine engine) {
		this.tree = tree;
		this.engine = engine;
		this.name = get("name",name);
		
		logger.info("starting: "+name);
		
		if(!isIn())
			matcher = readMatcher();
		
		if(!isOut())
			tagger = readTagger();

		if(isFilter()){
			checkLoopFree();
		}
		construct();
		
		if(!tree.isEmpty())
			warn("unknown values {0}", tree);
	}

	protected abstract boolean isIn();
	protected abstract boolean isOut();
	protected boolean isFilter() {
		return !(isIn()||isOut());
	}

	protected abstract void construct();

	protected void warn(String string, Object ...args) {
		logger.log(Level.WARNING,string,args);
	}

	
	private Tagger readTagger() {
		List tags = getArray("add-tag",Collections.EMPTY_LIST);
		
		if(isIn()){
			String type = get("type",name);
			return new InputTagger(type,tags);
		}
		
		rtags = getArray("remove-tag",Collections.EMPTY_LIST);
		
		if(tags.isEmpty() && rtags.isEmpty())
			return Tagger.empty;
		
		
		return new StandardTagger(tags,rtags);
	}

	private Matcher readMatcher() {
		tags = getArray("tags",Collections.EMPTY_LIST);
		String type = get("type",null);
		
		if(tags.isEmpty() && type.isEmpty())
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
	
	protected int getInt(String namex, int defaultv) {
		Object res = tree.remove(namex);
		
		if(res == null)
			return defaultv;
		if(res instanceof Number)
			return ((Number)res).intValue();
	
		
		return Integer.parseInt( (String) res);
	}


	

}
