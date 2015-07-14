package com.besaba.revonline.snippetide.application;

import com.besaba.revonline.snippetide.IdeController;
import com.besaba.revonline.snippetide.Main;
import com.besaba.revonline.snippetide.api.application.IDEApplication;
import com.besaba.revonline.snippetide.api.application.IDEInstanceContext;
import com.besaba.revonline.snippetide.api.events.manager.EventManager;
import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.plugins.Plugin;
import com.besaba.revonline.snippetide.api.plugins.PluginManager;
import com.besaba.revonline.snippetide.api.plugins.Version;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

public class IDEApplicationImpl implements IDEApplication {
  private final static Logger logger = Logger.getLogger(IDEApplicationImpl.class);

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
    final IdeController ideController = new IdeController(language, plugin, fileToOpen);

    loader.setControllerFactory(param -> param == IdeController.class ? ideController : null);

    final Scene scene;

    try {
      scene = new Scene(loader.load(Main.class.getResourceAsStream("ide.fxml")));
    } catch (IOException e) {
      final Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to open or create a new instance of the IDE :( Check logs", ButtonType.OK);
      alert.show();
      logger.fatal("Unable to create IDE instance!", e);
      return;
    }

    stage.setOnCloseRequest(event -> {
      logger.debug("IDEController " + ideController + " requested close, unregister controller to eventmanager");
      eventManager.unregisterListener(ideController);
    });

    stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
      logger.debug("IDEController " + ideController + " has focus? " + newValue);
      if (newValue) {
        eventManager.registerListener(ideController);
      } else {
        eventManager.unregisterListener(ideController);
      }
    });

    stage.setTitle("SnippetIDE " + (fileToOpen == null ? "" : fileToOpen.toString()));
    stage.setScene(scene);
    stage.show();
  }

  @NotNull
  @Override
  public Version getVersion() {
    return Version.parse("0.1");
  }
}
