package flens.filter;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import flens.core.Matcher;
import flens.core.Record;
import flens.core.SelfReporter;
import flens.core.Tagger;
import flens.filter.util.AbstractFilter;
import flens.typing.MetricForm;
import flens.typing.MetricType;

public class LZ4Filter extends  AbstractFilter implements SelfReporter{

	private static final MetricType INTYPE = new MetricType("flens.LZ4.in", "byte", "flens", MetricForm.Counter, 0, Long.MAX_VALUE, true);
	private static final MetricType OUTTYPE = new MetricType("flens.LZ4.out", "byte", "flens", MetricForm.Counter, 0, Long.MAX_VALUE, true);
	private static final MetricType COUNTTYPE = new MetricType("flens.LZ4.count", "record", "flens", MetricForm.Counter, 0, Long.MAX_VALUE, true);
	private boolean discard;

	public LZ4Filter(String name, String plugin, Tagger tagger, Matcher matcher, int prio,String infield,String outfield, boolean discard) {
		super(name, plugin, tagger, matcher, prio);
		this.infield=infield;
		this.outfield=outfield;
		this.discard = discard;
	}



	private LZ4Factory factory = LZ4Factory.fastestJavaInstance();
	private String infield;
	private String outfield;
	
	AtomicLong bytesin = new AtomicLong();
	AtomicLong bytesout  = new AtomicLong();
	AtomicLong count  = new AtomicLong(); 
	
	@Override
	public Collection<Record> process(Record in) {
		
		LZ4Compressor compressor =factory.fastCompressor();
		
		byte[] data = in.getBytes(infield);
		
		byte[] out = compressor.compress(data);
		
		bytesin.addAndGet(data.length);
		bytesout.addAndGet(out.length);
		count.addAndGet(1);
		
		if(discard)
			in.getValues().remove(infield);
		in.getValues().put(outfield, out);
		
		return Collections.emptyList();
	}

	@Override
	public void report(Set<Record> out) {
		
		out.add(Record.createFromTypeAndInstance(bytesin.get(),INTYPE,getName()));
		out.add(Record.createFromTypeAndInstance(bytesout.get(),OUTTYPE,getName()));
		out.add(Record.createFromTypeAndInstance(count.get(),COUNTTYPE,getName()));
		
	}

	
}
