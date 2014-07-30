package flens.output.util;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import flens.core.Flengine;
import flens.core.Matcher;
import flens.core.Output;
import flens.core.Record;
import flens.core.util.AbstractPlugin;

public class OutputQueueExposer extends AbstractPlugin implements Output {

	private Matcher matcher;
	private String name;
	private BlockingQueue<Record> queue = new LinkedBlockingQueue<>();
	
	
	
	public OutputQueueExposer(Matcher matcher, String name) {
		super();
		this.matcher = matcher;
		this.name = name;
	}

	private int sent;
	private int lost;	

	@Override
	public Matcher getMatcher() {
		return matcher;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public BlockingQueue<Record> getOutputQueue() {
		return queue ;
	}

	@Override
	public void start() {
		
	}

	@Override
	public void stop() {

	}

	@Override
	public int getRecordsSent() {
		return sent;
	}

	@Override
	public int getRecordsLost() {
		return lost;
	}
	
	public void markSent(){
		sent++;
	}
	
	public void markLost(){
		lost++;
	}

	@Override
	public boolean canUpdateConfig() {
		return false;
	}

	@Override
	public void updateConfig(Flengine engine, Map<String, Object> tree) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getPlugin() {
		return null;
	}

	@Override
	public void join() throws InterruptedException {
		
	}

}
