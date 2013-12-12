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

import java.util.LinkedList;
import java.util.List;

import flens.core.Config.Option;
import flens.core.util.AbstractConfig;
import flens.input.SelfMonitor;

public class PingQuery extends AbstractConfig{

	@Override
	protected void construct() {
		engine.addHandler(new flens.query.PingQuery(name));	
	}

	@Override
	protected boolean isIn() {
		return false;
	}

	@Override
	protected boolean isOut() {
		return false;
	}
	
	@Override
	protected boolean isQuery() {
		return true;
	}

	@Override
	public String getDescription() {
		return "ping query";
	}

}
