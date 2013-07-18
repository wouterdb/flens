package flens.core;

import java.util.Collection;
import java.util.Set;

public interface Filter extends Plugin {
	
	/**
	 * 
	 * make changes to the input record
	 * new records can be return
	 * 
	 * each record is passed along all filters in order
	 * new records go back to the start of the pipe. 
	 * 
	 * if the type of in is set to null, in is discarded
	 * 
	 * @param in
	 * @return
	 */
	public Collection<Record> process(Record in);
	
	public int priority();

}
