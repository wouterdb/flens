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

import oi.thekraken.grok.api.Grok;
import oi.thekraken.grok.api.Match;
import oi.thekraken.grok.api.exception.GrokException;

public class LogType {

    private String type;
    private String name;
    private String pattern;
    private Grok grok;

    public LogType(String name, String type, String pattern, Grok grok) throws GrokException {
        super();
        this.name = name;
        this.type = type;
        this.pattern = pattern;
        this.grok = grok;
        grok.compile(pattern);
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getPattern() {
        return pattern;
    }

    public Match match(String in) {
        return grok.match(in);
    }

}
