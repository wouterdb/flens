/*
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

package flens.typing;

import java.util.Set;

public class LogMatch {

    private Set<String> tags;
    private LogType owner;
    private boolean mustcontinue;

    public LogMatch(Set<String> tags, LogType owner, boolean mustcontinue) {
        super();
        
        this.tags = tags;
        this.owner = owner;
        this.mustcontinue = mustcontinue;
    }

   
    public Set<String> getTags() {
        return tags;
    }

    public LogType getOwner() {
        return owner;
    }

    public boolean mustcontinue() {
        return mustcontinue;
    }

    
  

}
