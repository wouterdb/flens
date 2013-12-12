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
