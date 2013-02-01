package flens.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class GSONTest {
	

	public static void main(String[] args) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		 Gson gson = new Gson();
		 System.out.println(gson.fromJson(new FileReader(new File("jsontest.json")), HashMap.class));
	}

}
