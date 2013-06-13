package flens.config;

import flens.core.Config;
import flens.core.Config.Option;
import flens.core.Flengine;
import flens.core.util.AbstractConfig;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SetFilter extends AbstractConfig
{
  protected boolean isIn()
  {
    return false;
  }

  protected void construct()
  {
    List f = getArray("fields", Collections.EMPTY_LIST);
    List t = getArray("values", Collections.EMPTY_LIST);
    if (f.size() != t.size()) {
      throw new IllegalArgumentException("to and from must be same size");
    }
    this.engine.addFilter(new flens.filter.SetFilter(this.name, this.tagger, this.matcher, f, t));
  }

  protected boolean isOut()
  {
    return false;
  }

  public String getDescription()
  {
    return "set fields";
  }

  public List<Config.Option> getOptions()
  {
    LinkedList<Option> out = new LinkedList<Option>(super.getOptions());
    out.add(new Config.Option("fields", "List", "[]", "field names"));
    out.add(new Config.Option("values", "List", "[]", "values"));
    return out;
  }
}