package flens.config;

import flens.core.Config;
import flens.core.Config.Option;
import flens.core.Flengine;
import flens.core.util.AbstractConfig;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RenameFilter extends AbstractConfig
{
  protected boolean isIn()
  {
    return false;
  }

  protected void construct()
  {
    List f = getArray("from", Collections.EMPTY_LIST);
    List t = getArray("to", Collections.EMPTY_LIST);
    if (f.size() != t.size()) {
      throw new IllegalArgumentException("to and from must be same size");
    }
    this.engine.addFilter(new flens.filter.RenameFilter(this.name, this.tagger, this.matcher,prio, f, t));
  }

  protected boolean isOut()
  {
    return false;
  }

  public String getDescription()
  {
    return "rrename fields";
  }

  public List<Config.Option> getOptions()
  {
    LinkedList<Option> out = new LinkedList<Option>(super.getOptions());
    out.add(new Config.Option("from", "List", "[]", "field names to change"));
    out.add(new Config.Option("to", "List", "[]", "names to change to"));
    return out;
  }
}