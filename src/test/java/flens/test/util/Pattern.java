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
        
package flens.test.util;

public class Pattern {

    int length; // in ms
    float msgrate; // in msg per sec
    String msg;
    boolean warmup = false;

    // distribution is not regular/bursty
    boolean nonnormal = false;

    boolean identicalMessage = false;

    public Pattern(int length, float msgrate, String msg) {
        super();
        this.length = length;
        this.msgrate = msgrate;
        this.msg = msg;
    }

    public Pattern(int length, float msgrate, String msg, boolean nonnormal) {
        super();
        this.length = length;
        this.msgrate = msgrate;
        this.msg = msg;
        this.nonnormal = nonnormal;
    }

    public Pattern() {
        this(1000, 10, "warmup");
        this.warmup = true;
    }

    public Pattern(int length, float msgrate, String msg, boolean nonnormal, boolean warmup) {
        this(length, msgrate, msg, nonnormal);
        this.warmup = warmup;
    }

    public Pattern(int length, float msgrate, String msg, boolean nonnormal, boolean warmup, boolean unchangedMessage) {
        this(length, msgrate, msg, nonnormal, warmup);
        this.identicalMessage = unchangedMessage;
    }

    public int getNrOfPackets() {
        return (int) (length * msgrate / 1000);
    }

    public String getMessage(int id) {
        if (identicalMessage) {
            return msg;
        } else {
            return msg + id;
        }
    }
}