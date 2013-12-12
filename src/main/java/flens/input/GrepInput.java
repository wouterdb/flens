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
package flens.input;

import java.io.File;
import java.util.regex.Pattern;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractActiveInput;

public class GrepInput extends AbstractActiveInput implements TailerListener{

	private Tailer tailer;
	private Pattern regex;
	private boolean tailFromEnd;
	private long delay = 1000;
	private String file;

	public GrepInput(String name, Tagger tagger, String file, String regex,boolean tail) {
		super(name, tagger);
		this.file=file;
		tailer = new Tailer(new File(file), this,delay ,tail);
		this.regex = Pattern.compile(regex); 
		this.tailFromEnd=tail;
	}

	@Override
	public void run() {
		tailer.run();
	}

	@Override
	public void stop() {
		tailer.stop();
		super.stop();
	}

	@Override
	public void init(Tailer tailer) {
	}

	@Override
	public void fileNotFound() {
		err("file not found: " + tailer.getFile().getName(),null);
	}

	@Override
	public void fileRotated() {
		
		
	}

	@Override
	public void handle(String line) {
		if(regex.matcher(line).matches())
			dispatch(Record.forLog(file,line));
		
	}

	@Override
	public void handle(Exception ex) {
		err("tailer failed: " + tailer.getFile().getName(),ex);
		
	}
}
