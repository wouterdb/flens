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
package flens.core.util;

import flens.core.Record;
import flens.core.Tagger;

import java.util.List;
import java.util.Map;

public class StandardTagger implements Tagger {
    private List<String> tags;
    private List<?> rtags;
    private String type;

    private final String configprefix;

    /**
     * Construct a full featured tagger. For input plugins,  input taggers are preferred. 
     * @param prefix this prefix is used when writing out the config for this tagger
     * @param type add the give type to records. If null, the old tpye is retained
     * @param tags  add the given tags
     * @param rtags remove the given tags (if present)
     */
    public StandardTagger(String prefix, String type, List<String> tags, List<?> rtags) {
        this.tags = tags;
        this.rtags = rtags;
        this.type = type;

        this.configprefix = prefix;
    }

    @Override
    public void adapt(Record record) {
        record.getTags().addAll(tags);
        record.getTags().removeAll(rtags);
        if (type != null) {
            record.setType(type);
        }
    }

    @Override
    public void outputConfig(Map<String, Object> tree) {
        tree.put(configprefix + "add-tags", tags);
        tree.put(configprefix + "set-type", type);
        tree.put(configprefix + "remove-tag", rtags);
    }

}
