package cn.dreampie.config;


import cn.dreampie.plugin.IPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Plugins.
 */
final public class Plugins {

  private final List<IPlugin> pluginList = new ArrayList<IPlugin>();

  public Plugins add(IPlugin plugin) {
    if (plugin != null)
      this.pluginList.add(plugin);
    return this;
  }

  public List<IPlugin> getPluginList() {
    return pluginList;
  }
}
