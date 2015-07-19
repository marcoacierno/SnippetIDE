package com.besaba.revonline.snippetide.api.run;

import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.plugins.Plugin;
import org.jetbrains.annotations.NotNull;

public class ManageRunConfigurationsContext {
  @NotNull
  private final Plugin plugin;
  @NotNull
  private final Language language;

  public ManageRunConfigurationsContext(@NotNull final Plugin plugin, @NotNull final Language language) {
    this.plugin = plugin;
    this.language = language;
  }

  @NotNull
  public Plugin getPlugin() {
    return plugin;
  }

  @NotNull
  public Language getLanguage() {
    return language;
  }
}
