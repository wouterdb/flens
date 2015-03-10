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

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatsdUtil {

    private static DateTimeFormatter format = DateTimeFormat.forPattern("dd/MMM/yyyy:HH:mm:ss Z");

    private Map<String, Object> out;

    private Set<String> tags;

    public StatsdUtil(Map<String, Object> out, Set<String> tags) {
        this.out = out;
        this.tags = tags;
    }

    public void count(String name) {
        tags.add("statsd");
        @SuppressWarnings("unchecked")
        List<String> list = (List<String>) out.get("statsd");
        if (list == null) {
            list = new LinkedList<String>();
            out.put("statsd", list);
        }
        list.add(name + ":1|c");
    }

    public Set<String> getTags() {
        return tags;
    }

}
