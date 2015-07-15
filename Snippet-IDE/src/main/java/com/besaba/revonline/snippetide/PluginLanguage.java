package com.besaba.revonline.snippetide;

import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.plugins.Plugin;

class PluginLanguage {
  private Language language;
  private Plugin plugin;

  public PluginLanguage(final Language language, final Plugin plugin) {
    this.language = language;
    this.plugin = plugin;
  }

  public Language getLanguage() {
    return language;
  }

  public Plugin getPlugin() {
    return plugin;
  }
}
