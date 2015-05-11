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

package flens.input;

import static flens.core.Constants.MESSAGE;
import static flens.core.Constants.TIME;

import flens.core.Record;
import flens.input.util.InputQueueExposer;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.LogRecord;

public class FlensLogHandler extends java.util.logging.Handler {

    private static final String LEVEL = "log-level";
    private static final String PARAMS = "parameters";
    private static final String SEQ_NR = "sequence-number";
    private static final String SOURCE_CLASS = "source-class";
    private static final String SOURCE_METHOD = "source-method";
    private static final String THREAD_ID = "thread-id";
    private static final String EXCEPTION = "exception";

    private InputQueueExposer queue;

    public FlensLogHandler(InputQueueExposer queue) {
        this.queue = queue;
    }

    @Override
    public void publish(LogRecord record) {
        if (queue.isStopped()) {
            Logger.getLogger("").removeHandler(this);
        }
        queue.send(convert(record));

    }

    private Record convert(LogRecord record) {
        Map<String, Object> value = new HashMap<>();
        value.put(TIME, record.getMillis());
        value.put(LEVEL, record.getLevel());
        value.put(MESSAGE, record.getMessage());
        value.put(PARAMS, record.getParameters());
        value.put(SEQ_NR, record.getSequenceNumber());
        value.put(SOURCE_CLASS, record.getSourceClassName());
        value.put(SOURCE_METHOD, record.getSourceMethodName());
        value.put(THREAD_ID, record.getThreadID());
        value.put(EXCEPTION, record.getThrown());
        return Record.createWithValues(value);

    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }

}
