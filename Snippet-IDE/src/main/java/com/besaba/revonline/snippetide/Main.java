package com.besaba.revonline.snippetide;

import com.besaba.revonline.snippetide.api.application.IDEApplication;
import com.besaba.revonline.snippetide.api.application.IDEInstanceContext;
import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.plugins.Plugin;
import com.besaba.revonline.snippetide.boot.Boot;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class Main extends Application {
  private final Boot boot = new Boot();
  private static Logger logger = Logger.getLogger(Main.class);
  private IDEApplication ideApplication;

  public static void main(String[] args) {
    Application.launch(Main.class, args);
  }

  @Override
  public void init() throws Exception {
    try {
      final String strApplicationDir = getParameters().getNamed().get("applicationdir");

      if (strApplicationDir != null) {
        final Path applicationDir = Paths.get(strApplicationDir);
        ideApplication = boot.boot(applicationDir, null, null, null);
      } else {
        ideApplication = boot.boot();
      }

      logApplicationStatus(ideApplication);
    } catch(Exception e) {
      notifyPreloader(new Preloader.ErrorNotification(null, "Boot exception: " + e.getMessage(), e));
      throw e;
    }
  }

  @Override
  public void start(final Stage primaryStage) throws Exception {
    final List<Plugin> plugins = ideApplication.getPluginManager().getPlugins();
    Language tempLanguage = null;
    Plugin firstPlugin = null;

    if (plugins.size() > 0) {
      firstPlugin = plugins.get(0);

      if (firstPlugin.getLanguages().size() > 0) {
        tempLanguage = firstPlugin.getLanguages().get(0);
      }
    }

    if (tempLanguage == null) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Ops");
      alert.setContentText("Ops... Looks like the application doesn't have any language. ");
      final ButtonType pluginsView = new ButtonType("Open Plugins view");
      alert.getButtonTypes().add(pluginsView);
      final ButtonType closeApplication = new ButtonType("Close application");
      alert.getButtonTypes().add(closeApplication);
      final Optional<ButtonType> response = alert.showAndWait();

      if (!response.isPresent()) {
        Platform.exit();
        return;
      }

      response.ifPresent(button -> {
        if (button != pluginsView) {
          return;
        }

        try {
          ideApplication.openPluginsList(null);
        } catch (IOException e) { }
      });
      return;
    }

    final Language randomLanguage = tempLanguage;
    final Plugin plugin = firstPlugin;

    final IDEInstanceContext ideInstanceContext = new IDEInstanceContext(randomLanguage, plugin, null);
    ideApplication.openIdeInstance(ideInstanceContext);
  }

  private void logApplicationStatus(final IDEApplication ideApplication) {
    logger.debug("--> LogApplicationStatus");
    logger.debug("Application launched with this information:");

    logger.debug("Application working directory: " + ideApplication.getApplicationDirectory());
    logger.debug("Application plugin directory: " + ideApplication.getPluginsDirectory());

    logger.debug("EventManager is " + ideApplication.getEventManager());
    logger.debug("PluginManager is " + ideApplication.getPluginManager());
  }

  @Override
  public void stop() throws Exception {
    boot.unboot();
  }
}
