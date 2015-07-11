package com.besaba.revonline.snippetide.application;

import com.besaba.revonline.snippetide.api.application.IDEApplication;
import com.besaba.revonline.snippetide.api.events.manager.EventManager;
import com.besaba.revonline.snippetide.api.plugins.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

public class IDEApplicationImpl implements IDEApplication {
  @NotNull
  private final EventManager eventManager;
  @NotNull
  private final PluginManager pluginManager;
  @NotNull
  private final Path applicationDirectory;
  @NotNull
  private final Path pluginsDirectory;

  public IDEApplicationImpl(@NotNull final EventManager eventManager,
                            @NotNull final PluginManager pluginManager,
                            @NotNull final Path applicationDirectory,
                            @NotNull final Path pluginsDirectory) {
    this.eventManager = eventManager;
    this.pluginManager = pluginManager;
    this.applicationDirectory = applicationDirectory;
    this.pluginsDirectory = pluginsDirectory;
  }

  @NotNull
  @Override
  public EventManager getEventManager() {
    return eventManager;
  }

  @NotNull
  @Override
  public PluginManager getPluginManager() {
    return pluginManager;
  }

  @NotNull
  @Override
  public Path getApplicationDirectory() {
    return applicationDirectory;
  }

  @NotNull
  @Override
  public Path getPluginsDirectory() {
    return pluginsDirectory;
  }
}
