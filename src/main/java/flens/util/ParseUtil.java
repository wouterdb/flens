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

package flens.util;

import flens.typing.MetricForm;

/**
 * Small tool for config parsing.
 *
 */
public class ParseUtil {

    /**
     * indicate an item that may occur, or be a wildcard ('*', '+'). Null for empty
     * and wildcard.
     */
    public static String may(String in) {
        if (in.equals("*") || in.equals("+")) {
            return null;
        }
        return in;
    }
    
    public static boolean isPlus(String in) {
        return in.equals("+") ;
    }
    

    public static String[] list(String in) {
        return in.split(",");
    }

    /**
     * a long integer item, if wildcard ('-') then return
     * Double.POSITIVE_INFINITY
     */
    public static Number nrHigh(String in) {
        if (in.equals("-")) {
            return Double.POSITIVE_INFINITY;
        }
        return Long.parseLong(in);

    }

    /**
     * a long integer item, if wildcard ('-') then return
     * Double.NEGATIVE_INFINITY
     */
    public static Number nrLow(String in) {
        if (in.equals("-")) {
            return Double.NEGATIVE_INFINITY;
        }
        return Long.parseLong(in);

    }

    public static Boolean bool(String in) {
        return Boolean.parseBoolean(in);
    }

    public static MetricForm form(String in) {
        return MetricForm.parse(in);
    }
}
