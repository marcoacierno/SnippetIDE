package com.besaba.revonline.snippetide.ui;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;

import java.util.stream.IntStream;

public class CodeTextArea extends TextArea {
  private final static KeyCodeCombination ENTER = new KeyCodeCombination(KeyCode.ENTER);


  public CodeTextArea() {
  }

  public CodeTextArea(final String text) {
    super(text);
  }

  {
    textProperty().addListener(this::onTextChanged);
    addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
  }

  public void onTextChanged(final ObservableValue<? extends String> value,
                            final String oldValue,
                            final String newValue) {

  }

  public void onKeyPressed(final KeyEvent event) {
    if (ENTER.match(event)) {
      // new line with same indentation level
      insertText(getCaretPosition(), "\n\t");

      event.consume();
    }
  }

}
