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

package flens.core.util;

import flens.config.util.Reflector;
import flens.core.Flengine;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractPlugin {

    public void warn(String line) {
        Logger.getLogger(getClass().getName()).log(Level.WARNING, getName() + ": " + line + "");
    }
    
    public void warn(String line,Object ... args) {
        Logger.getLogger(getClass().getName()).log(Level.WARNING, getName() + ": " + line ,args);
    }

    protected void warn(String msg, Exception exc) {
        Logger.getLogger(getClass().getName()).log(Level.WARNING, msg, exc);
    }

    protected void err(String msg, Throwable exc) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, msg, exc);
    }

    protected void err(String msg) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, msg);
    }

    protected void info(String msg) {
        Logger.getLogger(getClass().getName()).log(Level.INFO, msg);
    }

    public abstract String getName();

    public abstract String getPlugin();

    /**
     * Write back the running config for given engine to the given tree. Each
     * plugin is expected to add one entry of the form:
     * <code> tree.put(getName(), subtree); </code>
     * 
     */
    public void writeConfig(Flengine engine, Map<String, Object> tree) {
        String plug = getPlugin();
        if (plug == null) {
            return;
        }
        Map<String, Object> subtree = Reflector.store(this, engine.getPluginRepo().get(plug));
        tree.put(getName(), subtree);

    }

}
