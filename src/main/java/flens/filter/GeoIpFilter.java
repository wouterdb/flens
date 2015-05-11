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

package flens.filter;

import flens.core.Matcher;
import flens.core.Record;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GeoIpFilter extends AbstractFilter {
    private String field;
    private String outfield;
    private String database;
    private DatabaseReader reader;

    /**
     * @param name
     *            name under which this plugin is registered with the engine
     * @param plugin
     *            name of config that loaded this plugin (as registered in
     *            plugins.json)
     * @param tagger
     *            tagger used to mark output records
     * @param matcher
     *            matcher this filter should used to select recrods
     * @param prio
     *            plugin priority
     * @param infield  
     *            field to take input from (should contain IP or hostname)
     * @param outfield
     *          field to store output in
     * @param dbfile
     *          file containing geoip2 city database
     * @throws IOException
     *              could not load geoip database
     */

    public GeoIpFilter(String name, String plugin, Tagger tagger, Matcher matcher, int prio, String infield,
            String outfield, String dbfile) throws IOException {
        super(name, plugin, tagger, matcher, prio);
        this.field = infield;
        this.outfield = outfield;
        this.database = dbfile;

        start();

    }

    private void start() throws IOException {
        File database = new File(this.database);
        reader = new DatabaseReader.Builder(database).build();
    }

    @Override
    public Collection<Record> process(Record in) {

        try {
            String ip = (String) in.get(field);
            if (ip == null) {
                return Collections.emptyList();
            }
            InetAddress addr = InetAddress.getByName(ip);
            CityResponse cr = reader.city(addr);
            
            List<Double> location = new LinkedList<>();
            location.add(cr.getLocation().getLatitude());
            location.add(cr.getLocation().getLongitude());
            Map<String,Object> geojson = new HashMap<>();
            geojson.put("location",location );
            //geojson.put("accuracy",cr.getLocation().getAccuracyRadius() );
           
            in.getValues().put(outfield, geojson);
        } catch (Exception e) {
            warn("geoip failed ",e);
        }
        return Collections.emptyList();
    }
}
