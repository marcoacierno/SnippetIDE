package com.besaba.revonline.snippetide.run;

import javafx.util.Callback;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import org.controlsfx.property.editor.Editors;
import org.controlsfx.property.editor.PropertyEditor;

import java.io.File;
import java.nio.file.Path;

public class CustomPropertyEditorFactory implements Callback<PropertySheet.Item, PropertyEditor<?>> {
  private final Callback<PropertySheet.Item, PropertyEditor<?>> defaultFactory = new DefaultPropertyEditorFactory();

  @Override
  public PropertyEditor<?> call(final PropertySheet.Item param) {
    if (param.getType() == Path.class || param.getType() == File.class) {
      return createPathField(param);
    }

    return defaultFactory.call(param);
  }

  private PropertyEditor<?> createPathField(final PropertySheet.Item property) {
    return Editors.createTextEditor(property);
  }
}
