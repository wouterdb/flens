package flens.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import flens.config.util.AbstractConfig;
import flens.core.ConfigParser;
import flens.util.FileUtil;
import flens.util.MVELUtil;

public class CookbookConfig extends AbstractConfig {

	private static final String[] COOKBOOK = new String[] { ".",
			"/usr/share/flens/recipes", "/" };

	@Override
	protected boolean isIn() {
		return false;
	}

	@Override
	protected boolean isOut() {
		return false;
	}

	@Override
	protected boolean isQuery() {
		return false;
	}

	@Override
	protected boolean isFilter() {
		return false;
	}

	@Override
	protected void construct() {
		String template = get("template", null);
		boolean debug = getBool("DEBUG", false);
		if (template == null) {
			warn("no template given " + template);
			return;
		}

		try {
			InputStream f = FileUtil.findFileOrResource(COOKBOOK, template);

			CompiledTemplate compiled = MVELUtil.compileTemplateTooled(f);

			String newconfig = (String) TemplateRuntime.execute(compiled, tree);
			if(debug)
				System.out.println(newconfig);
			Gson g = new Gson();
			ConfigParser cp = new ConfigParser(engine);
			cp.construct(g.fromJson(newconfig, HashMap.class));
		} catch (FileNotFoundException e) {
			warn("could not open file ", e);
		}
		tree.clear();
	}

	@Override
	public List<Option> getOptions() {
		
		List<Option> opts = super.getOptions();
		opts.add(new Option("template", "String", "", "template file to use, file searchpath is " + Arrays.deepToString(COOKBOOK)));
		opts.add(new Option("DEBUG", "boolean", "false", "print out expanded config"));
		
		return opts;

	}
	

	@Override
	public String getDescription() {
		return "expand config templates and load config, extra params go to the template";
	}

}
