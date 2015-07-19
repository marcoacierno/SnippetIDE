package com.besaba.revonline.snippetide.api.application;

import com.besaba.revonline.snippetide.api.configuration.Configuration;
import com.besaba.revonline.snippetide.api.events.manager.EventManager;
import com.besaba.revonline.snippetide.api.plugins.PluginManager;
import com.besaba.revonline.snippetide.api.plugins.Version;
import com.besaba.revonline.snippetide.api.run.ManageRunConfigurationsContext;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;

public interface IDEApplication {
  @NotNull
  EventManager getEventManager();

  @NotNull
  PluginManager getPluginManager();

  @NotNull
  Configuration getConfiguration();

  @NotNull
  Path getApplicationDirectory();

  @NotNull
  Path getPluginsDirectory();

  @NotNull
  Path getTemporaryDirectory();

  @NotNull
  Path getConfigurationFile();

  @NotNull
  Path getDefaultConfigurationFile();

  void openIdeInstance(final IDEInstanceContext IdeInstanceContext);

  void openAboutWindow(@Nullable Window window) throws IOException;

  void openPluginsList(@Nullable Window window) throws IOException;

  void openKeymapSetting(@Nullable Window window) throws IOException;

  void openManageConfigurations(@NotNull final ManageRunConfigurationsContext runConfigurationsContext, @Nullable final Window window) throws IOException;

  @NotNull
  Version getVersion();
}
