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
package flens.config;

import flens.config.util.AbstractConfig;
import flens.core.ConfigParser;
import flens.util.FileUtil;
import flens.util.MvelUtil;

import com.google.gson.Gson;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CookbookConfig extends AbstractConfig {

    private static final String[] COOKBOOK = new String[] { ".", "/usr/share/flens/recipes", "/" };

    @Override
    protected boolean isIn() {
        return false;
    }

    @Override
    protected boolean isOut() {
        return false;
    }

    @Override
    protected boolean isQuery() {
        return false;
    }

    @Override
    protected boolean isFilter() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void construct() {
        String template = get("template", null);
        boolean debug = getBool("DEBUG", false);
        if (template == null) {
            warn("no template given " + template);
            return;
        }

        try {
            InputStream file = FileUtil.findFileOrResource(COOKBOOK, template);

            CompiledTemplate compiled = MvelUtil.compileTemplateTooled(file);

            String newconfig = (String) TemplateRuntime.execute(compiled, tree);
            if (debug) {
                System.out.println(newconfig);
            }
            Gson gson = new Gson();
            ConfigParser cp = new ConfigParser(engine);
            cp.construct(gson.fromJson(newconfig, HashMap.class));
        } catch (FileNotFoundException e) {
            warn("could not open file ", e);
        }
        tree.clear();
    }

    @Override
    public List<Option> getOptions() {

        List<Option> opts = new LinkedList<>(super.getOptions());
        opts.add(new Option("template", "String", "", "template file to use, file searchpath is "
                + Arrays.deepToString(COOKBOOK)));
        opts.add(new Option("DEBUG", "boolean", "false", "print out expanded config"));

        return opts;

    }

    @Override
    public String getDescription() {
        return "expand config templates and load config, extra params go to the template";
    }

}
