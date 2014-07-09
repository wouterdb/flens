package flens.input;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import flens.core.Record;
import flens.input.util.InputQueueExposer;
import static flens.core.Constants.*;

public class FlensLogHandler extends java.util.logging.Handler {


	private static final String LEVEL = "log-level";
	private static final String PARAMS = "parameters";
	private static final String SEQ_NR = "sequence-number";
	private static final String SOURCE_CLASS = "source-class";
	private static final String SOURCE_METHOD = "source-method";
	private static final String THREAD_ID = "thread-id";
	private static final String EXCEPTION = "exception";
	
	
	private InputQueueExposer queue;

	public FlensLogHandler(InputQueueExposer queue) {
		this.queue=queue;
	}
	

	@Override
	public void publish(LogRecord record) {
		if(queue.isStopped())
			Logger.getLogger("").removeHandler(this);
		queue.send(convert(record));

	}

	private Record convert(LogRecord record) {
		Map<String,Object> value = new HashMap();
		value.put(TIME, record.getMillis());
		value.put(LEVEL, record.getLevel());
		value.put(MESSAGE,record.getMessage());
		value.put(PARAMS,record.getParameters());
		value.put(SEQ_NR,record.getSequenceNumber());
		value.put(SOURCE_CLASS,record.getSourceClassName());
		value.put(SOURCE_METHOD,record.getSourceMethodName());
		value.put(THREAD_ID, record.getThreadID());
		value.put(EXCEPTION, record.getThrown());
		return Record.createWithValues(value);
		
	}


	@Override
	public void flush() {

	}

	@Override
	public void close() throws SecurityException {

	}

}
