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

import java.util.Queue;

public interface Output extends Plugin {

	/**
	 * idempotent, fast
	 */
	public Matcher getMatcher();
	
	/**
	 * @return the output queue
	 * 
	 * method is idempotent
	 */
	public Queue<Record> getOutputQueue();
	public void start();
	public void stop();
	public void join() throws InterruptedException;
	
	
	//stats, may be an underestimate, due to threading
	//prefer loss to lock
	public int getRecordsSent();
	public int getRecordsLost();
	
}
