package com.besaba.revonline.snippetide.application;

import com.besaba.revonline.snippetide.api.application.IDEApplication;
import com.besaba.revonline.snippetide.api.events.manager.EventManager;
import com.besaba.revonline.snippetide.api.plugins.PluginManager;
import org.jetbrains.annotations.NotNull;

public class IDEApplicationImpl implements IDEApplication {
  @NotNull
  private final EventManager eventManager;
  @NotNull
  private final PluginManager pluginManager;

  public IDEApplicationImpl(@NotNull final EventManager eventManager, @NotNull final PluginManager pluginManager) {
    this.eventManager = eventManager;
    this.pluginManager = pluginManager;
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
}
