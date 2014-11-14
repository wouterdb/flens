package flens.input.collectd;

import static org.junit.Assert.*;

import org.junit.Test;

public class CollectdTypeTableTest {

	@Test
	public void testResolveStringString() {
		CollectdTypeingTable ct = new CollectdTypeingTable();
		
		assertArrayEquals(new String[]{"collectd.if_octets.rx", "collectd.if_octets.tx"},ct.resolve("interface","if_octets",null).names);
		
		
	}

}
