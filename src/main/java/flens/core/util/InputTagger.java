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

public class InputTagger implements Tagger {

    private String type;
    private List<String> tags;
    private final String configprefix;

    /**
     * Construct an input tagger.
     * @param prefix this prefix is used when writing out the config for this tagger
     * @param type add the give type to records. Should not be null
     * @param tags add the given tags to the records
     * 
     * @throws IllegalArgumentException when the type is null
     */
    public InputTagger(String prefix, String type, List<String> tags) {
        if (type == null) {
            throw new IllegalArgumentException("no type given");
        }
        this.type = type;
        this.tags = tags;
        this.configprefix = prefix;
    }

    @Override
    public void adapt(Record rec) {
        rec.getTags().addAll(tags);
        rec.setType(type);
    }

    @Override
    public void outputConfig(Map<String, Object> tree) {
        tree.put(configprefix + "add-tags", tags);
        tree.put(configprefix + "type", type);

    }

}
