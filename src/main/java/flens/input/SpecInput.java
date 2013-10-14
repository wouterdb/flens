package flens.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import flens.core.Record;
import flens.core.Tagger;
import flens.input.util.AbstractPeriodicInput;

public class SpecInput extends AbstractPeriodicInput{
	
	private Map<String,Spec> allspecs = new HashMap<>();
	private List<Spec> specs;

	public SpecInput(String name, Tagger tagger, int interval, List<String> specs) {
		super(name, tagger, interval);
		if(interval<1000)
			throw new IllegalArgumentException("time under 1000ms is too short to run all tests");
		List<Spec> allspecs = collectAllSpecs();
		for(Spec s:allspecs){
			this.allspecs.put(s.getName(),s);
		}
		activate(specs);
	}


	/**
	 * extend here!
	 * 
	 * @return
	 */
	protected List<Spec> collectAllSpecs() {
		List<Spec> specs = new LinkedList<Spec>();
		specs.add(new SpecDisk());
		specs.add(new SpecDiskRead());
		specs.add(new SpecCPU());
		specs.add(new SpecExec());
		specs.add(new SpecSleep());
		return specs;
		
	}

	private void activate(List<String> specs) {
		this.specs = new LinkedList<Spec>();
		for (String name : specs) {
			Spec s = this.allspecs.get(name);
			if(s==null)
				warn(String.format("spec %s not found, options are %s",name,allspecs.keySet().toString()));
			else
				this.specs.add(s);
		}
	}

	public interface Spec {
		public void run() throws Exception;
		public String getName();
	}
	
	public class SpecDisk implements Spec{

		private static final int DISK_BYTES = 1000;
		private String metric = SpecInput.this.getName()+"."+getName();
		
		@Override
		public void run() throws IOException {
			long now = System.nanoTime();
			File f = File.createTempFile("flens-spec", "test");
			OutputStream out = new FileOutputStream(f);
			for(int i =0; i<DISK_BYTES;i++)
				out.write(i);
			out.flush();
			out.close();
			long delta = System.nanoTime() - now;
			f.delete();
			dispatch(Record.createWithValue(metric, delta));
		}

		@Override
		public String getName() {
			return "write";
		}
		
		@Override
		public String toString() {
			return getName();
		}
	}
	
	public class SpecDiskRead implements Spec{

		private static final int DISK_BYTES = 1000;
		private String metric = SpecInput.this.getName()+"."+getName();
		
		@Override
		public void run() throws IOException {
			long now = System.nanoTime();
			File f = new File("/usr/bin/bash");
			InputStream in = new FileInputStream(f);
			byte[] bytes = new byte[DISK_BYTES];
			IOUtils.read(in, bytes);
			in.close();
			long delta = System.nanoTime() - now;
			dispatch(Record.createWithValue(metric, delta));
		}

		@Override
		public String getName() {
			return "read";
		}
		
		@Override
		public String toString() {
			return getName();
		}
	}
	
	public class SpecExec implements Spec{

		private static final String CMD = "/usr/bin/true";
		private String metric = SpecInput.this.getName()+"."+getName();
		
		@Override
		public void run() throws IOException, InterruptedException {
			ProcessBuilder pb = new ProcessBuilder(CMD);
			long now = System.nanoTime();
			pb.inheritIO();
			Process p = pb.start();
			p.waitFor();
			long delta = System.nanoTime() - now;
			dispatch(Record.createWithValue(metric, delta));
		}

		@Override
		public String getName() {
			return "exec";
		}
		
		@Override
		public String toString() {
			return getName();
		}
	}
	
	public class SpecSleep implements Spec{

		private static final int interval = 100;
		private String metric = SpecInput.this.getName()+"."+getName();
		
		@Override
		public void run() throws IOException, InterruptedException {
			
			long now = System.nanoTime();
			Thread.sleep(interval);
			long delta = System.nanoTime() - now-(interval*1000000);
			dispatch(Record.createWithValue(metric, delta));
		}

		@Override
		public String getName() {
			return "sleep";
		}
		
		@Override
		public String toString() {
			return getName();
		}
	}
	
	public class SpecCPU implements Spec{

		private static final int CPU_NUMBER = 66667;
		private String metric = SpecInput.this.getName()+"."+getName();
		
		@Override
		public void run() throws IOException {
			long now = System.nanoTime();
			long x = factor(CPU_NUMBER);
			long delta = System.nanoTime() - now;
			
			dispatch(Record.createWithValue(metric, delta));
		}

		@Override
		public String getName() {
			return "cpu";
		}
		
		@Override
		public String toString() {
			return getName();
		}
		
		public long factor(long n) { 
	    	long out = 0;
	        
	        // for each potential factor i
	        for (long i = 2; i <= n / i; i++) {

	            // if i is a factor of N, repeatedly divide it out
	            while (n % i == 0) {
	            	out+=i;
	                n = n / i;
	            }
	        }

	        // if biggest factor occurs only once, n > 1
	        if (n > 1) out+=n;
	        
	        return out;
	    }

	}

	@Override
	protected TimerTask getWorker() {
		return new TimerTask() {
			
			@Override
			public void run() {
				for (Spec spec : specs) {
					try{
						spec.run();
					}catch(Exception e){
						err("spec test failed ", e);
					}
				}
				
			}
		};
	}

}
