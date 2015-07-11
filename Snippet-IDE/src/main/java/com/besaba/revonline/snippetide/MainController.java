package com.besaba.revonline.snippetide;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import org.apache.log4j.Logger;

import java.nio.file.Path;

public class MainController {
  private final static Logger logger = Logger.getLogger(MainController.class);

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
