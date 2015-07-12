package com.besaba.revonline.snippetide.api.application;

import com.besaba.revonline.snippetide.api.events.manager.EventManager;
import com.besaba.revonline.snippetide.api.plugins.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public interface IDEApplication {
  @NotNull
  EventManager getEventManager();

  @NotNull
  PluginManager getPluginManager();

  @NotNull
  Path getApplicationDirectory();

  @NotNull
  Path getPluginsDirectory();

  @NotNull
  Path getTemporaryDirectory();

  void openIdeInstance(final IDEInstanceContext IdeInstanceContext);
}
