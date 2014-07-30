package flens.test.util;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import flens.core.Input;
import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractActiveInput;

public class PatternInput extends AbstractActiveInput implements PatternStore{
	
	ReentrantLock lock = new ReentrantLock();
	Condition c = lock.newCondition();

	public PatternInput(String name, String plugin, Tagger tagger) {
		super(name, plugin, tagger);
	}

	private List<Pattern> sequence = new LinkedList<>();

	@Override
	public void run() {
		long start = System.nanoTime();
		
		lock.lock();
		
		try {
			for (Pattern p : sequence) {
				
				int nrofpackets = p.getNrOfPackets();
				long delay = p.length*1000000L / nrofpackets;
				for (int i = 0; i < nrofpackets; i++) {
					dispatch(Record.forLog(p.msg + i));
					long now = System.nanoTime();
					c.awaitNanos(start + delay - now);
					//System.out.println(start + delay - now);
					//Thread.sleep(start + delay - now);
					start += delay;
				}

				
			}

		} catch (InterruptedException e) {
			System.out.println(e);
		}

		lock.unlock();
		running = false;

	}
	
	public void addPattern(Pattern p){
		sequence.add(p);
	}

}
