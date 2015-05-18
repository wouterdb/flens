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
        
package flens.filter;

import flens.core.Constants;
import flens.core.Filter;
import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class UnBatchingFilter extends AbstractFilter implements Filter {

    private boolean keep;

    public UnBatchingFilter(String name, String plugin, Tagger tagger, Matcher matcher, int prio, boolean keep) {
        super(name, plugin, tagger, matcher, prio);
        this.keep = keep;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Record> process(Record in) {
        List<Record> outs = new LinkedList<Record>();

        if (in.getValues().containsKey(Constants.SUBRECORDS)) {
            List<Record> list = (List<Record>) in.getValues().get(Constants.SUBRECORDS);
            for (Record record : list) {
                outs.add(tag(record));
            }
            if (!keep) {
                in.setType(null);
            }
        }

        return outs;
    }

}
