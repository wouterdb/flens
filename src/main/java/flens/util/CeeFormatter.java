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
        
package flens.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
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
import java.util.logging.LogRecord;

public class CeeFormatter extends Formatter {

    private final Date dat = new Date();

    public synchronized String format(LogRecord record) {
        StringBuilder json = new StringBuilder("@cee:{");
        // based on SimpleFormatter

        // time
        dat.setTime(record.getMillis());
        json.append(String.format("\"time\":%tQ", dat));

        // message
        String message = formatMessage(record);
        json.append(",\"msg\":\"");
        json.append(message.replace("\"", "\\\""));
        json.append("\"");

        // level
        json.append(",\"java.level\":\"");
        json.append(record.getLevel().getName());
        json.append("\"");

        //
        if (record.getSourceClassName() != null) {
            json.append(",\"java.class\":\"");
            json.append(record.getSourceClassName());
            json.append("\"");
        }
        if (record.getSourceMethodName() != null) {
            json.append(",\"java.method\":\"");
            json.append(record.getSourceMethodName());
            json.append("\"");
        }

        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            record.getThrown().printStackTrace(pw);
            pw.close();
            String throwable = sw.toString().replace("\n", ",").replace("\t", "  ");
            throwable = throwable.substring(0, throwable.length() - 1);
            json.append(",\"java.exception\":\"");
            json.append(throwable);
            json.append("\"");
        }
        json.append("}");
        return String.format("%tQ %s %s %s %s%n", dat, record.getLevel().getName(), record.getLoggerName(), message,
                json.toString());
    }

}
