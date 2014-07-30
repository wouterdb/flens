package flens.test.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.lang3.tuple.Pair;

import static org.junit.Assert.*;
import flens.core.Constants;
import flens.core.Flengine;
import flens.core.Matcher;
import flens.core.Output;
import flens.core.Record;
import flens.core.util.AbstractPlugin;
import flens.core.util.AllMatcher;
import flens.output.util.AbstractPumpOutput;

public class PatternOutput extends AbstractPumpOutput implements Output, PatternStore {

	public PatternOutput(String name, String plugin) {
		super(name, plugin, new AllMatcher());
	}
	
	public PatternOutput(String name, String plugin,Matcher m) {
		super(name, plugin, m);
	}



	@Override
	public boolean canUpdateConfig() {
		return false;
	}

	@Override
	public void updateConfig(Flengine engine, Map<String, Object> tree) {
		throw new Error("This should NEVER be used in production");
	}


	private List<Pattern> sequence = new LinkedList<>();
	private LinkedList<Pair<Long, Record>> q = new LinkedList<>();

	public void addPattern(Pattern p) {
		sequence.add(p);
	}

	@Override
	public void run() {
		while (running) {
			try {
				Record r = queue.take();
				sent++;
				q.add(Pair.of(System.nanoTime(), r));
			} catch (InterruptedException e) {
				// normal for stop
				stop();
			}
			
		}
	}
	
	public void analyze(float deltarate, int deltalength) {
		int current = 0;
		
		float prev = Float.MAX_VALUE;
		
		for (Pattern p : sequence) {
			if(Math.abs(p.msgrate- prev) < deltarate){
				System.out.println("packet rates can not be distinguished" + p);
			}
			prev = p.msgrate;
		}
		
		for(Pair<Long,Record> rec:q){
			System.out.println(rec.getKey() + "\t" + rec.getValue().getValues().get(Constants.TIME)+ "\t" + rec.getValue().getValues().get("message"));
		}
		
		for (Pattern p : sequence) {
			int expectedNroFPackets = p.getNrOfPackets();
			int deltapacks = (int)(deltalength * p.msgrate / 1000);
			
			int i;
			float rate=0;
			
			if(p.warmup){
				current += expectedNroFPackets;
				continue;
			}
			
			if(p.nonnormal){
				if(deltalength!=0)
					throw new IllegalArgumentException("non normal but no fixed length, not implemented");
				long now = q.get(current).getKey();
				
				long next = q.get(Math.min(current+p.getNrOfPackets(),q.size()-1)).getKey();
				
				long time = next - now;
				rate = 1000000000.0f/time*p.getNrOfPackets();
				assertEquals(p.msgrate, rate,deltarate);
				
				current += p.getNrOfPackets();
				continue;
			}
			
			//normal
			for(i = 0;i<expectedNroFPackets+deltapacks && i+current+1<q.size();i++){
				long now = q.get(i+current).getKey();
				long next = q.get(i+current+1).getKey();
				long time = next - now;
				rate = 1000000000.0f/time;
				
				if(!( Math.abs(p.msgrate- rate) < deltarate)){
					System.out.println(String.format("rate change at packet %d (%d) to %f (expected %f)",i,current,rate,p.msgrate));
					break;
				}
					
				
			}
			
			//last one is one short, due to interval calc
			if(i+current+1==q.size())
				assertEquals("incorrect nr of packets, got rate " + rate ,p.getNrOfPackets(),i+1,deltalength);
			else
				assertEquals("incorrect nr of packets, got rate " + rate + " (" + p.msgrate + ")"  ,p.getNrOfPackets(),i,deltalength);
			
			current += i;
		}
		
		
	}


}
