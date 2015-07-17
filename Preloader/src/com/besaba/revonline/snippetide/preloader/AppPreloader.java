package com.besaba.revonline.snippetide.preloader;

import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppPreloader extends Preloader {
  @Override
  public void start(final Stage primaryStage) throws Exception {
    final Scene scene = new Scene((Parent) FXMLLoader.load(AppPreloader.class.getResource("preloader.fxml")));
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
