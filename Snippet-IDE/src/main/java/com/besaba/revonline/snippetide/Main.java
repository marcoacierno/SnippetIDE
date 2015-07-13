package com.besaba.revonline.snippetide;

import com.besaba.revonline.snippetide.api.application.IDEApplication;
import com.besaba.revonline.snippetide.api.application.IDEInstanceContext;
import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.plugins.Plugin;
import com.besaba.revonline.snippetide.boot.Boot;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.util.List;

public class Main extends Application {
  private final Boot boot = new Boot();
  private Logger logger = Logger.getLogger(Main.class);

  public static void main(String[] args) {
    Application.launch(Main.class, args);
  }

  @Override
  public void start(final Stage primaryStage) throws Exception {
    final IDEApplication ideApplication = boot.boot();
    logApplicationStatus(ideApplication);

    final List<Plugin> plugins = ideApplication.getPluginManager().getPlugins();
    Language tempLanguage = null;
    Plugin firstPlugin = null;

    if (plugins.size() > 0) {
      firstPlugin = plugins.get(0);

      if (firstPlugin.getLanguages().size() > 0) {
        tempLanguage = firstPlugin.getLanguages().get(0);
      }
    }

    if (tempLanguage == null || firstPlugin == null) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Ops");
      alert.setContentText("Ops... Looks like the application doesn't have any language. ");
      alert.getButtonTypes().add(new ButtonType("Close application"));
      alert.showAndWait();
      Platform.exit();
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
