package com.besaba.revonline.snippetide.application;

import com.besaba.revonline.snippetide.IdeController;
import com.besaba.revonline.snippetide.Main;
import com.besaba.revonline.snippetide.api.application.IDEApplication;
import com.besaba.revonline.snippetide.api.application.IDEInstanceContext;
import com.besaba.revonline.snippetide.api.configuration.Configuration;
import com.besaba.revonline.snippetide.api.events.manager.EventManager;
import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.plugins.Plugin;
import com.besaba.revonline.snippetide.api.plugins.PluginManager;
import com.besaba.revonline.snippetide.api.plugins.Version;
import com.besaba.revonline.snippetide.api.run.ManageRunConfigurationsContext;
import com.besaba.revonline.snippetide.run.ManageRunConfigurationsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
  @NotNull
  private final Configuration configuration;
  @NotNull
  private final Path configurationFile;
  @NotNull
  private final Path defaultConfigurationFile;

  public IDEApplicationImpl(@NotNull final EventManager eventManager,
                            @NotNull final PluginManager pluginManager,
                            @NotNull final Path applicationDirectory,
                            @NotNull final Path pluginsDirectory,
                            @NotNull final Path temporaryDirectory,
                            @NotNull final Configuration configuration,
                            @NotNull final Path configurationFile,
                            @NotNull final Path defaultConfigurationFile) {
    this.eventManager = eventManager;
    this.pluginManager = pluginManager;
    this.applicationDirectory = applicationDirectory;
    this.pluginsDirectory = pluginsDirectory;
    this.temporaryDirectory = temporaryDirectory;
    this.configuration = configuration;
    this.configurationFile = configurationFile;
    this.defaultConfigurationFile = defaultConfigurationFile;
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
  public Configuration getConfiguration() {
    return configuration;
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

  @NotNull
  @Override
  public Path getConfigurationFile() {
    return configurationFile;
  }

  @NotNull
  @Override
  public Path getDefaultConfigurationFile() {
    return defaultConfigurationFile;
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

  @Override
  public void openAboutWindow(@Nullable final Window window) {
    try {
      final Stage stage = new Stage();

      final FXMLLoader loader = new FXMLLoader(IdeController.class.getResource("."));
      final Scene scene = new Scene(loader.load(IdeController.class.getResourceAsStream("about.fxml")));
      scene.getStylesheets().add(IdeController.class.getResource("about.css").toExternalForm());
      scene.setOnKeyPressed(event -> {
        if (event.getCode() == KeyCode.ESCAPE) {
          stage.close();
        }
      });
      stage.focusedProperty().addListener(((observable, oldValue, newValue) -> {
        if (!newValue) {
          stage.close();
        }
      }));

      ((Text) scene.getRoot().lookup("#versionText")).setText("Version: " + getVersion().toString());

      stage.initStyle(StageStyle.UNDECORATED);
      stage.initOwner(window);
      stage.setResizable(false);
      stage.setScene(scene);

      stage.show();
    } catch (IOException e) {
      new Alert(Alert.AlertType.ERROR, "Unable to open about window", ButtonType.OK).show();
      logger.error("Failed to open about window", e);
    }
  }

  @Override
  public void openPluginsList(@Nullable final Window window) {
    try {
      final Stage stage = new Stage();
      final Scene scene = new Scene(FXMLLoader.load(IdeController.class.getResource("pluginslist.fxml")));

      stage.initModality(Modality.WINDOW_MODAL);
      stage.initOwner(window);
      stage.setScene(scene);
      stage.setTitle("Plugins");

      stage.show();
    } catch (IOException e) {
      new Alert(Alert.AlertType.ERROR, "Unable to open plugins list", ButtonType.OK).show();
      logger.error("Failed to open plugins list stage", e);
    }
  }

  @Override
  public void openKeymapSetting(@Nullable final Window window) throws IOException {
    final FXMLLoader loader = new FXMLLoader(IdeController.class.getResource("keymap/"));
    final Stage stage = new Stage();
    final Scene scene = new Scene(loader.load(IdeController.class.getResourceAsStream("keymap/keymap.fxml")));

    stage.initModality(Modality.WINDOW_MODAL);
    stage.initOwner(window);
    stage.setResizable(false);
    stage.setScene(scene);
    stage.setTitle("Keymap settings");

    stage.show();
  }

  @Override
  public void openManageConfigurations(@NotNull final ManageRunConfigurationsContext runConfigurationsContext,
                                       @Nullable final Window window)
      throws IOException {
    final FXMLLoader loader = new FXMLLoader(IdeController.class.getResource("runconfigurations/"));
    loader.setControllerFactory(param -> param == ManageRunConfigurationsController.class ?
        new ManageRunConfigurationsController(runConfigurationsContext) :
        null
    );
    final Stage stage = new Stage();
    final Scene scene = new Scene(
        loader.load(IdeController.class.getResourceAsStream("runconfigurations/managerunconfigurations.fxml"))
    );

    stage.initModality(Modality.WINDOW_MODAL);
    stage.initOwner(window);
    stage.setResizable(false);
    stage.setScene(scene);
    stage.setTitle("Manage run configurations for " + runConfigurationsContext.getLanguage().getName());

    stage.show();
  }

  @NotNull
  @Override
  public Version getVersion() {
    return Version.parse("0.1");
  }
}
