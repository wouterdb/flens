package flens.test.util;

public class Pattern {

	int length; // in ms
	float msgrate; // in msg per sec
	String msg;

	public Pattern(int length, float msgrate, String msg) {
		super();
		this.length = length;
		this.msgrate = msgrate;
		this.msg = msg;
	}

	
	public int getNrOfPackets(){
		return (int) (length * msgrate / 1000);
	}
}