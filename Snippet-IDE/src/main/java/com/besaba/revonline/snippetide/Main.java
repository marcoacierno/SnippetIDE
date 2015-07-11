package com.besaba.revonline.snippetide;

import com.besaba.revonline.snippetide.api.application.IDEApplication;
import com.besaba.revonline.snippetide.boot.Boot;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

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

    final Scene scene = new Scene(FXMLLoader.load(Main.class.getResource("ide.fxml")));

    primaryStage.setScene(scene);
    primaryStage.show();
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
