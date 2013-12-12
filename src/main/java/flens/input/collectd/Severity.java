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
package flens.input.collectd;


public enum Severity {
    FAILURE(1),
    WARNING(2),
    UNKNOWN(3),
    OKAY(4);

    private static final Severity[] lookup = {UNKNOWN, FAILURE, WARNING, UNKNOWN, OKAY};
    private static final String[] names = {FAILURE.name(), WARNING.name(), UNKNOWN.name(), OKAY.name()};

    public static String[] names() {
        return names;
    }

    public static Severity find(int severity) {
        if (severity > 0 && severity < lookup.length) {
            return lookup[severity];
        }
        return UNKNOWN;
    }

    public final int id;

    Severity(int severity) {
        this.id = severity;
    }

}