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

package flens.typing.scripting;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MvelUtil {

    private Map<String, Object> out;

    private Set<String> tags;

    public MvelUtil(Map<String, Object> out, Set<String> tags) {
        this.out = out;
        this.tags = tags;
    }

    public void remove(String name) {
        out.remove(name);
    }

    public Set<String> getTags() {
        return tags;
    }

    public void removeNulls() {
        List<String> badkeys = new LinkedList<>();
        for (Map.Entry<String, Object> pair : out.entrySet()) {
            if (pair.getValue() == null) {
                badkeys.add(pair.getKey());
            }
        }
        
        for (String key : badkeys) {
            out.remove(key);
        }
    }

}
