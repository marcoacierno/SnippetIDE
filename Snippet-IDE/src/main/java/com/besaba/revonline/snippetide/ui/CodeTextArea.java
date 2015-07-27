package com.besaba.revonline.snippetide.ui;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextArea;

public class CodeTextArea extends TextArea {

  public CodeTextArea() {
  }

  public CodeTextArea(final String text) {
    super(text);
  }

  {
    textProperty().addListener(this::onTextChanged);
  }

  public void onTextChanged(final ObservableValue<? extends String> value,
                            final String oldValue,
                            final String newValue) {
  }
}
