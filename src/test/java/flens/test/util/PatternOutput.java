package flens.test.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.lang3.tuple.Pair;
import static org.junit.Assert.*;

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

	private String name;
	private String plugin;

	@Override
	public boolean canUpdateConfig() {
		return false;
	}

	@Override
	public void updateConfig(Flengine engine, Map<String, Object> tree) {
		throw new Error("This should NEVER be used in production");
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPlugin() {
		return plugin;
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
				q.add(Pair.of(System.currentTimeMillis(), r));
			} catch (InterruptedException e) {
				// normal for stop
				stop();
			}
			
		}
	}
	
	public void analyze(float deltarate) {
		int current = 0;
		
		/*long last = q.peek().getKey();
		for (Pair<Long,Record> pair : q) {
			System.out.println(pair.getKey() + " " + (pair.getKey()-last));
			last = pair.getKey();
		}*/
		for (Pattern p : sequence) {
			long start = q.get(current).getKey();
			long end = q.get(current + p.getNrOfPackets()-1).getKey();
			long time = end-start;
			float rate = 1000.0f*p.getNrOfPackets()/time;
			current += p.getNrOfPackets();
			System.out.println(time+ " " + p.getNrOfPackets()+ " " +rate);
			assertEquals("message rate not as expected",p.msgrate,rate,deltarate);
		}
		
		
	}


}
