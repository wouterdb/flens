package flens.config.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import org.mvel2.MVEL;
import org.mvel2.templates.CompiledTemplate;

import flens.core.Matcher;
import flens.core.Output;
import flens.core.Record;
import flens.core.util.AbstractPlugin;
import flens.input.util.InputQueueExposer;
import flens.output.util.AbstractPumpOutput;
import flens.util.MVELUtil;

public class AlertOutput extends AbstractPumpOutput implements Output {

	public class AlertTimer extends TimerTask {

		@Override
		public void run() {
			in.send(new Record(msg));

		}

	}

	private int interval;
	private String script;
	private InputQueueExposer in;
	private Serializable compiled;
	private Timer t = new Timer();
	private AlertTimer tt;
	private String msg;

	public AlertOutput(String name, Matcher matcher, int interval, String script, String msg, InputQueueExposer inpex) {
		super(name, matcher);
		this.interval = interval;
		this.script = script;
		this.in = inpex;
		this.msg = msg;
		this.compiled = MVEL.compileExpression(script, MVELUtil.getTooledContext());

	}

	@Override
	public void start() {
		super.start();
		restartTimer();
	}
	
	public synchronized void restartTimer(){
		if(tt!=null)
			tt.cancel();
		tt = new AlertTimer();
		t.schedule(tt, interval,interval);
	}

	@Override
	public void run() {
		try {
			while (true) {
				Record r = queue.take();
				try{
					process(r);
				}catch(Exception e){
					err("fault in script",e);
				}
			}
		} catch (InterruptedException e) {
			// normal for stop
			stop();
		}
	}

	private void process(Record in) {
		
		Object result = MVEL.executeExpression(compiled, in.getValues());

		if (!(result instanceof Boolean)){
			warn("did not return boolean " + result.getClass().getName());
			return;
		}
		

		boolean br = (Boolean)result;
		
		if(br){
			restartTimer();
		}
	}

}
