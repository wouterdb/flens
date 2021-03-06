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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NamedThreadFactory implements ThreadFactory {
    public class LogExcpetionHandler implements UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread tr, Throwable ex) {
            Logger.getLogger("flens.util.NamedThreadFactory").log(Level.SEVERE,
                    "thread " + tr.getName() + " was killed by exception", ex);

        }

    }

    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    /**
     * @param perfix
     *            prefix to put before thread name.
     */
    public NamedThreadFactory(String perfix) {
        SecurityManager sec = System.getSecurityManager();
        group = (sec != null) ? sec.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = perfix;

    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(group, runnable, namePrefix + threadNumber.getAndIncrement(), 0);
        if (thread.isDaemon()) {
            thread.setDaemon(false);
        }
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }

        thread.setUncaughtExceptionHandler(new LogExcpetionHandler());
        return thread;
    }
}
