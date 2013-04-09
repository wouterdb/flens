import java.util.Properties;


public class Env {

	
	public static void main(String[] args) {
		Properties p = System.getProperties();
		p.list(System.out);
	}
}
