package com.besaba.revonline.snippetide;

import com.besaba.revonline.snippetide.api.language.Language;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * The controller of the view ide.fxml
 */
public class IdeController {
  private final static Logger logger = Logger.getLogger(IdeController.class);
  @NotNull
  private final Language language;
  @FXML
  private Text languageName;

  /**
   * @param language What will be the language used by this view?
   */
  public IdeController(@NotNull final Language language) {
    this.language = language;
  }

  @FXML
  private TextArea codeArea;

  public void initialize() {
    languageName.setText(language.getName());
  }

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
