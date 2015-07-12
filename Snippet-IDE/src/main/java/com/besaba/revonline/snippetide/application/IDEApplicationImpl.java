package com.besaba.revonline.snippetide.application;

import com.besaba.revonline.snippetide.IdeController;
import com.besaba.revonline.snippetide.Main;
import com.besaba.revonline.snippetide.api.application.IDEApplication;
import com.besaba.revonline.snippetide.api.application.IDEInstanceContext;
import com.besaba.revonline.snippetide.api.events.manager.EventManager;
import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.plugins.Plugin;
import com.besaba.revonline.snippetide.api.plugins.PluginManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

public class IDEApplicationImpl implements IDEApplication {
  @NotNull
  private final EventManager eventManager;
  @NotNull
  private final PluginManager pluginManager;
  @NotNull
  private final Path applicationDirectory;
  @NotNull
  private final Path pluginsDirectory;
  @NotNull
  private final Path temporaryDirectory;

  public IDEApplicationImpl(@NotNull final EventManager eventManager,
                            @NotNull final PluginManager pluginManager,
                            @NotNull final Path applicationDirectory,
                            @NotNull final Path pluginsDirectory,
                            @NotNull final Path temporaryDirectory) {
    this.eventManager = eventManager;
    this.pluginManager = pluginManager;
    this.applicationDirectory = applicationDirectory;
    this.pluginsDirectory = pluginsDirectory;
    this.temporaryDirectory = temporaryDirectory;
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

  @NotNull
  @Override
  public Path getTemporaryDirectory() {
    return temporaryDirectory;
  }

  @Override
  public void openIdeInstance(final IDEInstanceContext context) {
    final Language language = context.getLanguage();
    final Plugin plugin = context.getPlugin();
    final Path fileToOpen = context.getOriginalFile();

    final Stage stage = new Stage();
    final FXMLLoader loader = new FXMLLoader(Main.class.getResource("ide.fxml"));

    loader.setControllerFactory(param -> param == IdeController.class ? new IdeController(language, plugin, fileToOpen) : null);

    final Scene scene;

    try {
      scene = new Scene(loader.load(Main.class.getResourceAsStream("ide.fxml")));
    } catch (IOException e) {
      final Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to open or create a new instance of the IDE :( Check logs", ButtonType.OK);
      alert.show();
      return;
    }

    stage.setTitle("SnippetIDE " + (fileToOpen == null ? "" : fileToOpen.toString()));
    stage.setScene(scene);
    stage.show();
  }
}
