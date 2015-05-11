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

package flens.pairs;



import flens.core.Flengine;
import flens.core.PluginRepo;
import flens.core.util.InputTagger;
import flens.core.util.StandardMatcher;
import flens.core.util.TypeTagger;
import flens.filter.CharacterDecoder;
import flens.filter.LZ4CompressionFilter;
import flens.filter.LZ4DecompressionFilter;
import flens.test.util.DataHeavyPattern;
import flens.test.util.Pattern;
import flens.test.util.PatternInput;
import flens.test.util.PatternOutput;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;



public class LZ4Test{

    @Test
    public void testBatcherPair() throws InterruptedException {
        PatternInput pinp = new PatternInput("pinp", "pinp", new InputTagger("", "test",
                Collections.<String>emptyList()));
        
        LZ4CompressionFilter comp = new LZ4CompressionFilter("compress", "LZ4Compression",
                new TypeTagger("", "compressed"),
                new StandardMatcher("test",Collections.<String>emptyList()),
                10,"message","body",true);
        
        LZ4DecompressionFilter decomp = new LZ4DecompressionFilter("decompress", "LZ4Compression",
                new TypeTagger("", "decompressed"),
                new StandardMatcher("compressed",Collections.<String>emptyList()),
                20,"body","body",false);
        
        CharacterDecoder dec = new CharacterDecoder("decode", "decode",  
                new TypeTagger("", "decoded"),
                new StandardMatcher("decompressed",Collections.<String>emptyList()),
                30);
        
        PatternOutput poutp = new PatternOutput("poutp", "poutp", new StandardMatcher("decoded",
                Collections.<String>emptyList()));


        Flengine testenFlengine = new Flengine(new PluginRepo());
        testenFlengine.setPoolSize(1);

        testenFlengine.addInput(pinp);
        testenFlengine.addFilter(comp);
        testenFlengine.addFilter(decomp);
        testenFlengine.addFilter(dec);
        testenFlengine.addOutput(poutp);
        //testenFlengine.addOutput(new SystemOut("out", "out", new AllMatcher()));

        loadPattern1(pinp, poutp);

        testenFlengine.start();
        pinp.join();
        
        testenFlengine.stop();

        System.out.println(pinp.getRecordsSent());
        System.out.println(comp.getRecordsSent());
        System.out.println(decomp.getRecordsSent());
       
        System.out.println(poutp.getRecordsSent());

        poutp.analyzeContent();
    }



    private void loadPattern1(PatternInput pinp, PatternOutput poutp) {
        // warmup
        pinp.addPattern(new Pattern());
        poutp.addPattern(new Pattern());
        
        pinp.addPattern(new Pattern(5000, 40, "per time"));
        poutp.addPattern(new Pattern(5000, 40, "per time"));

    }
    
    
    @Test
    public void testBatcherPair2() throws InterruptedException, NoSuchAlgorithmException {
        PatternInput pinp = new PatternInput("pinp", "pinp", new InputTagger("", "test",
                Collections.<String>emptyList()));
        
        LZ4CompressionFilter comp = new LZ4CompressionFilter("compress", "LZ4Compression",
                new TypeTagger("", "compressed"),
                new StandardMatcher("test",Collections.<String>emptyList()),
                10,"message","body",true);
        
        LZ4DecompressionFilter decomp = new LZ4DecompressionFilter("decompress", "LZ4Compression",
                new TypeTagger("", "decompressed"),
                new StandardMatcher("compressed",Collections.<String>emptyList()),
                20,"body","body",false);
        
        CharacterDecoder dec = new CharacterDecoder("decode", "decode",  
                new TypeTagger("", "decoded"),
                new StandardMatcher("decompressed",Collections.<String>emptyList()),
                30);
        
        PatternOutput poutp = new PatternOutput("poutp", "poutp", new StandardMatcher("decoded",
                Collections.<String>emptyList()));


        Flengine testenFlengine = new Flengine(new PluginRepo());
        testenFlengine.setPoolSize(1);

        testenFlengine.addInput(pinp);
        testenFlengine.addFilter(comp);
        testenFlengine.addFilter(decomp);
        testenFlengine.addFilter(dec);
        testenFlengine.addOutput(poutp);
        //testenFlengine.addOutput(new SystemOut("out", "out", new AllMatcher()));

        loadPattern2(pinp, poutp);

        testenFlengine.start();
        pinp.join();
        Thread.sleep(1000);
        testenFlengine.stop();

        System.out.println(pinp.getRecordsSent());
        System.out.println(comp.getRecordsSent());
        System.out.println(decomp.getRecordsSent());
       
        System.out.println(poutp.getRecordsSent());

        poutp.analyzeContent();
    }

    private void loadPattern2(PatternInput pinp, PatternOutput poutp) throws NoSuchAlgorithmException {
        // warmup
        pinp.addPattern(new Pattern());
        poutp.addPattern(new Pattern());
        
        pinp.addPattern(new DataHeavyPattern(5000, 40));
        poutp.addPattern(new DataHeavyPattern(5000, 40));

    }
    


}
