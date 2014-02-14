package flens.input.collectd;

import static org.junit.Assert.*;

import org.junit.Test;

public class CollectdExpansionTableTest {

	@Test
	public void testResolveStringString() {
		CollectdExpansionTable ct = new CollectdExpansionTable();
		
		assertArrayEquals(new String[]{"in", "out"},ct.resolve("interface","a"));
		assertArrayEquals(new String[]{"used", "free"},ct.resolve("df","df"));
		
	}

}
