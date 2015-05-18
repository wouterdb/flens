/*
 *
 *     Copyright 2013-2015 KU Leuven Research and Development - iMinds - Distrinet
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

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ElasticSearchUtil {

    private static DateTimeFormatter format = ISODateTimeFormat.dateTime();

    private Map<String, Object> out;

    private Set<String> tags;

    public ElasticSearchUtil(Map<String, Object> out, Set<String> tags) {
        this.out = out;
        this.tags = tags;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public void clean() {
        List<String> badkeys = new LinkedList<String>();
        for (String key : out.keySet()) {
            if (key.startsWith("@")) {
                badkeys.add(key);
            }
        }

        for (String key : badkeys) {
            out.put(key.substring(1), out.remove(key));
        }

    }

    public void cleanTime() {
        out.put("time", format.parseMillis((String) out.remove("@timestamp")));
    }

    public Set<String> getTags() {
        return tags;
    }

}
