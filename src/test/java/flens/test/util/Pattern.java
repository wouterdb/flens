package flens.test.util;

public class Pattern {

	int length; // in ms
	float msgrate; // in msg per sec
	String msg;
	boolean warmup = false;
	
	//distribution is not regular/bursty
	boolean nonnormal = false;

	public Pattern(int length, float msgrate, String msg) {
		super();
		this.length = length;
		this.msgrate = msgrate;
		this.msg = msg;
	}
	
	

	public Pattern(int length, float msgrate, String msg, boolean nonnormal) {
		super();
		this.length = length;
		this.msgrate = msgrate;
		this.msg = msg;
		this.nonnormal = nonnormal;
	}



	public Pattern(){
		this(1000,10,"warmup");
		this.warmup=true;
	}
	
	public Pattern(int length, float msgrate, String msg, boolean nonnormal, boolean warmup) {
		this(length,msgrate,msg,nonnormal);
		this.warmup=warmup;
	}



	public int getNrOfPackets(){
		return (int) (length * msgrate / 1000);
	}
}