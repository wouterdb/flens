package flens.test.util;

import java.util.LinkedList;
import java.util.List;

import flens.core.Input;
import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractActiveInput;

public class PatternInput extends AbstractActiveInput implements PatternStore{

	public PatternInput(String name, String plugin, Tagger tagger) {
		super(name, plugin, tagger);
	}

	private List<Pattern> sequence = new LinkedList<>();

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		
		try {
			for (Pattern p : sequence) {
				
				int nrofpackets = p.getNrOfPackets();
				int delay = p.length / nrofpackets;
				for (int i = 0; i < nrofpackets; i++) {
					dispatch(Record.forLog(p.msg + i));
					long now = System.currentTimeMillis();
					Thread.sleep(start + delay - now);
					start += delay;
				}

				
			}

		} catch (InterruptedException e) {
			System.out.println(e);
		}

		running = false;

	}
	
	public void addPattern(Pattern p){
		sequence.add(p);
	}

}
