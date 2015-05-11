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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class GrokUtil {

    private static DateTimeFormatter format = DateTimeFormat.forPattern("dd/MMM/yyyy:HH:mm:ss Z");

    private Map<String, Object> out;

    private Set<String> tags;

    public GrokUtil(Map<String, Object> out, Set<String> tags) {
        this.out = out;
        this.tags = tags;
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

  //CHECKSTYLE:OFF
    public void splitKV(String value, String linesep, String sep) {
        for (String pair : value.split(linesep)) {
            String[] kv = pair.split(sep, 2);
            if (kv.length == 1) {
                out.put(kv[0].trim(), "");
            } else {
                out.put(kv[0].trim(), kv[1].trim());
            }
        }
    }
  //CHECKSTYLE:ON

    public void cleanTime() {
        out.remove("TIME");
        Object year = out.remove("YEAR");
        if (year == null) {
            year = new DateTime().getYear();
        }
        String month = (String) out.remove("MONTH");
        Object day = out.remove("MONTHDAY");

        Object hour = out.remove("HOUR");
        if (hour == null) {
            return;
        }
        Object min = out.remove("MINUTE");
        Object sec = out.remove("SECOND");

        Object tz = out.remove("INT");
        if (tz instanceof Integer) {
            tz = String.format("%+05d", tz);
        } else if (tz == null) {
            tz = "+0000";
        }

        // "dd/MMM/yyyy:HH:mm:ss Z"

        out.put("time",
                format.parseMillis(String.format("%s/%s/%s:%s:%s:%s %s", day, month, year, hour, min, sec, tz)));
    }

    public Set<String> getTags() {
        return tags;
    }

}
