package com.besaba.revonline.snippetide;

import com.besaba.revonline.snippetide.boot.Boot;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {
  private final Boot boot = new Boot();

  public static void main(String[] args) {
    Application.launch(Main.class, args);
  }

  @Override
  public void start(final Stage primaryStage) throws Exception {
    boot.boot();

    final Scene scene = new Scene(FXMLLoader.load(Main.class.getResource("ide.fxml")));

    primaryStage.setScene(scene);
    primaryStage.show();
  }

  @Override
  public void stop() throws Exception {
    boot.unboot();
  }
}
