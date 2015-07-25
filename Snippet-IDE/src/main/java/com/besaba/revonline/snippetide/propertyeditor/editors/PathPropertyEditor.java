package com.besaba.revonline.snippetide.propertyeditor.editors;

import com.besaba.revonline.snippetide.ui.FileDirectoryChooserControl;
import javafx.beans.value.ObservableValue;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.AbstractPropertyEditor;

import java.nio.file.Path;

public class PathPropertyEditor extends AbstractPropertyEditor<Path, FileDirectoryChooserControl> {
  public PathPropertyEditor(PropertySheet.Item property) {
    super(property, new FileDirectoryChooserControl(FileDirectoryChooserControl.Chooser.FILE));
  }

  @Override
  public void setValue(Path path) {
    getEditor().pathProperty().setValue(path);
  }

  @Override
  protected ObservableValue<Path> getObservableValue() {
    return getEditor().pathProperty();
  }
}
