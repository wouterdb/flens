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
package flens.input.util;

import java.util.concurrent.BlockingQueue;

import flens.core.Input;
import flens.core.Record;
import flens.core.Tagger;
import flens.core.util.AbstractPlugin;

public abstract class AbstractInput extends AbstractPlugin implements Input{

	protected BlockingQueue<Record> in;
	protected Tagger tagger;
	private String name;
	private int sent;

	public AbstractInput(String name,Tagger tagger) {
		this.name = name;
		this.tagger = tagger;
	}

	
	@Override
	public String getName() {
		return name;
	}
	
	protected void dispatch(Record r){
		tagger.adapt(r);
		in.add(r);
		sent++;
	}
	
	public void setInputQueue(BlockingQueue<Record> queue) {
		this.in = queue;
	}
	
	public int getRecordsSent() {
		return sent;
	}
}
