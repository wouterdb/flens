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
package flens.config;

import java.util.List;

import flens.core.util.AbstractConfig;



public class Unpacker extends AbstractConfig{
	
	enum Modes{
		
	}

	@Override
	protected boolean isIn() {
		
		return false;
	}
	
	@Override
	protected void construct() {
		requiresLoopFree();
		engine.addFilter(new flens.filter.Unpacker(name,tagger,matcher,prio));
	}

	

	@Override
	protected boolean isOut() {
		return false;
	}

	@Override
	public List<Option> getOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
