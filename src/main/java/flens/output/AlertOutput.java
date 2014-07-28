package flens.output;

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
import flens.output.util.AbstractPeriodicOutput;
import flens.output.util.AbstractPumpOutput;
import flens.util.MVELUtil;

public class AlertOutput extends AbstractPeriodicOutput implements Output {

	public class AlertTimer extends TimerTask {

		@Override
		public void run() {
			in.send(new Record(msg));

		}

	}

	private String script;
	private Serializable compiled;
	private String msg;

	public AlertOutput(String name,String plugin, Matcher matcher, int interval, String script, String msg, InputQueueExposer inpex) {
		super(name,plugin, matcher,interval,inpex);
		this.script = script;
		this.msg = msg;
		this.compiled = MVEL.compileExpression(script, MVELUtil.getTooledContext());
	}


	

	protected void process(Record in) {
		
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




	@Override
	protected TimerTask createTimerTask() {
		return new AlertTimer();
	}

}
