package com.besaba.revonline.snippetide;

import com.besaba.revonline.snippetide.boot.Boot;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {
  private static final Path APPLICATION_PATH = Paths.get( System.getProperty("user.dir") , "SnippetIDE" );

  public static void main(String[] args) {
    Boot.boot(APPLICATION_PATH);

    Application.launch(Main.class, args);
  }

  @Override
  public void start(final Stage primaryStage) throws Exception {
    final Scene scene = new Scene(FXMLLoader.load(Main.class.getResource("ide.fxml")));

    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
