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

public class GrokUtil {

    private static DateTimeFormatter format = DateTimeFormat.forPattern("dd/MMM/yyyy:HH:mm:ss Z");

    private Map<String, Object> out;

    private Set<String> tags ;

    public GrokUtil(Map<String, Object> out, Set<String> tags) {
        this.out = out;
        this.tags=tags;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public void clean() {
        for (String x : new LinkedList<String>(out.keySet())) {
            if (x.toUpperCase().equals(x)) {
                out.remove(x);
            }
        }
    }

    public void cleanTime() {
        out.remove("MONTH");
        out.remove("HOUR");
        out.remove("YEAR");
        out.remove("MINUTE");
        out.remove("TIME");
        out.remove("SECOND");
        out.remove("MONTHDAY");
        out.remove("INT");
        out.put("time", format.parseMillis((String) out.get("timestamp")));
    }

    public Set<String> getTags() {
        return tags;
    }

}
