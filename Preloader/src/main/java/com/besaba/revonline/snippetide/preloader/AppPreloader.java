package com.besaba.revonline.snippetide.preloader;

import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.PrintWriter;
import java.io.StringWriter;

public class AppPreloader extends Preloader {
  private ProgressBar progressBar;
  private Stage preloaderStage;

  @Override
  public void start(final Stage primaryStage) throws Exception {
    primaryStage.initStyle(StageStyle.UNDECORATED);

    final Parent parent = FXMLLoader.load(AppPreloader.class.getResource("preloader.fxml"));
    final Scene scene = new Scene(parent);

    progressBar = (ProgressBar) parent.getChildrenUnmodifiable().get(1);
    progressBar.setProgress(0);
    preloaderStage = primaryStage;

    scene.getStylesheets().add(AppPreloader.class.getResource("preloader.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.setResizable(false);
    primaryStage.show();
  }

  @Override
  public void handleProgressNotification(final ProgressNotification info) {
    progressBar.setProgress(info.getProgress());
  }

  @Override
  public boolean handleErrorNotification(final ErrorNotification info) {
    final Throwable cause = info.getCause();

    final StringWriter stringWriter = new StringWriter();
    final PrintWriter printWriter = new PrintWriter(stringWriter);
    info.getCause().printStackTrace(printWriter);

    new Alert(Alert.AlertType.ERROR, "Error during boot!\n" + stringWriter.toString(), ButtonType.OK).showAndWait();
    return true;
  }

  @Override
  public void handleStateChangeNotification(final StateChangeNotification info) {
    if (info.getType() == StateChangeNotification.Type.BEFORE_INIT) {
      progressBar.setVisible(true);
    } else if (info.getType() == StateChangeNotification.Type.BEFORE_START) {
      preloaderStage.hide();
    }
  }
}
