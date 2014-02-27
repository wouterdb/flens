package flens.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class FileUtil {
	
	public static File findFile(List<String> searchpath,String name){
		for (String prefix : searchpath) {
			File f = new File(prefix,name);
			if(f.exists())
				return f;
		}
		
	
		
		return null;
	}

	public static File findFile(String[] searchpath, String name) {
		for (String prefix : searchpath) {
			File f = new File(prefix,name);
			if(f.exists())
				return f;
		}
		return null;
	}

	public static InputStream findFileOrResource(String[] searchpath, String name) throws FileNotFoundException {
		for (String prefix : searchpath) {
			File f = new File(prefix,name);
			if(f.exists())
				return new FileInputStream(f);
		}
		
		return FileUtil.class.getClassLoader().getResourceAsStream(name);
		
	}
	
	
}
