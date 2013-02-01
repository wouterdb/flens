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
	 * @param in
	 * @return
	 */
	public Collection<Record> process(Record in);

}
