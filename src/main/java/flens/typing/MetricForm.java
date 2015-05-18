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
        
package flens.typing;

public enum MetricForm {

    Gauge, Counter, /**
     *  is for counters which get reset upon reading.[bad]
     */
    Absolute, Other;

    /**
     * Parse the shorthand to the from.
     * (g,c,a,o)
     */
    public static MetricForm parse(String in) {
        in = in.substring(0, 1).toLowerCase();
        if (in.startsWith("g")) {
            return Gauge;
        } else if (in.startsWith("c")) {
            return Counter;
        } else if (in.startsWith("a")) {
            return Absolute;
        } else if (in.startsWith("o")) {
            return Other;
        }

        throw new IllegalArgumentException("form unkown: " + in);
    }
    
    public String toShortString() {
        return name().substring(0, 1).toLowerCase();
    }

}
