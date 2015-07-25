package com.besaba.revonline.snippetide.ui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileDirectoryChooserControl extends HBox {
  @NotNull
  private final Chooser chooser;

  public static enum Chooser {
    FILE,
    DIRECTORY
  }

  @FXML
  private TextField manualPath;

  private final SimpleObjectProperty<Path> path = new SimpleObjectProperty<>();

  public FileDirectoryChooserControl(@NotNull final Chooser chooser) {
    final FXMLLoader loader = new FXMLLoader(FileDirectoryChooserControl.class.getResource("filedirectorychooser.fxml"));
    loader.setRoot(this);
    loader.setController(this);

    this.chooser = chooser;

    try {
      loader.load();
    } catch (IOException e) {
      throw new AssertionError(e);
    }

    path.addListener(this::pathChanged);
    manualPath.textProperty().addListener(this::manualPathChanged);
  }

  private void pathChanged(final ObservableValue<? extends Path> observableValue,
                           final Path oldValue,
                           final Path newValue) {
    manualPath.setText(newValue.toString());
  }

  private void manualPathChanged(final ObservableValue<? extends String> observableValue,
                           final String oldValue,
                           final String newValue) {
    path.set(Paths.get(newValue));
  }

  public Path getPath() {
    return path.get();
  }

  public SimpleObjectProperty<Path> pathProperty() {
    return path;
  }

  @FXML
  private void openChooser() {
    final File result;

    if (chooser == Chooser.FILE) {
      result = openFileChooser();
    } else if (chooser == Chooser.DIRECTORY) {
      result = openDirectoryChooser();
    } else {
      throw new AssertionError();
    }

    if (result != null) {
      path.set(result.toPath());
    }
  }

  private File openDirectoryChooser() {
    final DirectoryChooser fileChooser = new DirectoryChooser();
    return fileChooser.showDialog(manualPath.getScene().getWindow());
  }

  private File openFileChooser() {
    final FileChooser fileChooser = new FileChooser();
    return fileChooser.showOpenDialog(manualPath.getScene().getWindow());
  }
}
