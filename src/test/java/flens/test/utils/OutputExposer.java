package flens.test.utils;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import flens.core.Flengine;
import flens.core.Matcher;
import flens.core.Output;
import flens.core.Record;
import flens.core.util.AbstractPlugin;
import flens.output.util.AbstractPumpOutput;

public class OutputExposer extends AbstractPlugin implements Output{

	private Queue<Record> queue=new LinkedList<>();
	private Matcher matcher;
	private String name;
	
	public OutputExposer(String name, Matcher matcher) {
		this.name = name;
		this.matcher = matcher;
	}

	@Override
	public boolean canUpdateConfig() {
		return false;
	}

	@Override
	public void updateConfig(Flengine engine, Map<String, Object> tree) {
		
	}

	@Override
	public Matcher getMatcher() {
		return matcher;
	}

	@Override
	public Queue<Record> getOutputQueue() {
		return queue;
	}

	@Override
	public void start() {
		
	}

	@Override
	public void stop() {
	}

	@Override
	public int getRecordsSent() {
		return queue.size();
	}

	@Override
	public int getRecordsLost() {
		return 0;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPlugin() {
		return "test";
	}



}
