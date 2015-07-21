package com.besaba.revonline.snippetide.run.editors;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.AbstractPropertyEditor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class PathPropertyEditor extends AbstractPropertyEditor<Path, TextField> {
  private SimpleObjectProperty<Path> valueProperty;

  public PathPropertyEditor(PropertySheet.Item property) {
    super(property, new TextField());
    final TextField editor = getEditor();

    editor.setEditable(false);

    editor.setOnMouseClicked(event -> {
      final FileChooser fileChooser = new FileChooser();
      final Optional<File> optionalFile = Optional.ofNullable(fileChooser.showOpenDialog(editor.getScene().getWindow()));

      optionalFile.ifPresent(file -> valueProperty.setValue(file.toPath()));
    });

    valueProperty.addListener(((observable, oldValue, newValue) -> {
      if (newValue == null) {
        return;
      }

      getEditor().setText(newValue.toString());
    }));
  }

  @Override
  public void setValue(Path path) {
    valueProperty.set(path);
  }

  @Override
  protected ObservableValue<Path> getObservableValue() {
    if (valueProperty == null) {
      valueProperty = new SimpleObjectProperty<>(Paths.get(getEditor().getSelectedText()));
    }

    return valueProperty;
  }
}
