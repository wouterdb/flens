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
package flens.filter.util;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import flens.core.Filter;
import flens.core.Input;
import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.core.util.AbstractPlugin;

public abstract class AbstractFilter extends AbstractPlugin implements Filter {

	private Tagger tagger;
	private Matcher matcher;
	private String name;
	private int prio;

	public AbstractFilter(String name, Tagger tagger, Matcher matcher, int prio) {
		this.name = name;
		this.tagger = tagger;
		this.matcher = matcher;
		this.prio = prio;
	}

	@Override
	public String getName() {
		return name;
	}

	protected Record tag(Record r) {
		tagger.adapt(r);
		return r;
	}

	@Override
	public Matcher getMatcher() {
		return matcher;
	}

	@Override
	public int priority() {
		return prio;
	}

}
