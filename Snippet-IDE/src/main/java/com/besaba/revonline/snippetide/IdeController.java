package com.besaba.revonline.snippetide;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import org.apache.log4j.Logger;

public class IdeController {
  private final static Logger logger = Logger.getLogger(IdeController.class);

  @FXML
  private TextArea codeArea;

  public void initialize() { }

  public void onKeyPressed(Event event) {
    final KeyEvent keyEvent = (KeyEvent) event;

    switch (keyEvent.getCode()) {
      case F5: {
        compile();
        break;
      }
    }
  }

  private void compile() {
    logger.info("Pressed compile key");

  }
}
