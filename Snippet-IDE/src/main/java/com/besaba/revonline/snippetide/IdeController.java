package com.besaba.revonline.snippetide;

import com.besaba.revonline.snippetide.api.application.IDEApplication;
import com.besaba.revonline.snippetide.api.application.IDEApplicationLauncher;
import com.besaba.revonline.snippetide.api.compiler.CompilationProblem;
import com.besaba.revonline.snippetide.api.compiler.CompilationProblemType;
import com.besaba.revonline.snippetide.api.compiler.CompilationResult;
import com.besaba.revonline.snippetide.api.events.compile.CompileFinishedEvent;
import com.besaba.revonline.snippetide.api.events.compile.CompileStartEvent;
import com.besaba.revonline.snippetide.api.events.compile.CompileStartEventBuilder;
import com.besaba.revonline.snippetide.api.events.manager.EventManager;
import com.besaba.revonline.snippetide.api.language.Language;
import com.google.common.eventbus.Subscribe;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;
import org.controlsfx.control.Notifications;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * The controller of the view ide.fxml
 */
public class IdeController {
  public static final String DEFAULT_SNIPPET_FILE_NAME = "Solution";

  private final static Logger logger = Logger.getLogger(IdeController.class);

  @NotNull
  private final Language language;
  @FXML
  private Text languageName;
  @NotNull
  private final EventManager eventManager = IDEApplicationLauncher.getIDEApplication().getEventManager();
  @NotNull
  private final IDEApplication application = IDEApplicationLauncher.getIDEApplication();
  @FXML
  private TextArea codeArea;

  // <editor-fold name="Compilation table fields">
  @FXML
  private TableView<CompilationProblem> compilationTable;
  @FXML
  private TableColumn<CompilationProblem, CompilationProblemType> compilationTableType;
  @FXML
  private TableColumn<CompilationProblem, Long> compilationTableLine;
  @FXML
  private TableColumn<CompilationProblem, String> compilationTableMessage;
  // </editor-fold>

  /**
   * @param language What will be the language used by this view?
   */
  public IdeController(@NotNull final Language language) {
    this.language = language;
  }

  public void initialize() {
    languageName.setText(language.getName());
    eventManager.registerListener(this);

    prepareCompilationTable();
  }

  private void prepareCompilationTable() {
    compilationTableType.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getType()));
    compilationTableLine.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getLine()));
    compilationTableMessage.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getMessage()));
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

    // if the code is too big what will happen?
    final String sourceText = codeArea.getText();
    final Path sourceFile = Paths.get(
        application.getTemporaryDirectory().toString(),
        DEFAULT_SNIPPET_FILE_NAME + "." + language.getExtensions()[0]
    );

    if (!tryToWriteSourceToFile(sourceText, sourceFile)) {
      return;
    }

    final CompileStartEvent event = new CompileStartEventBuilder()
        .setTarget(language)
        .setSourceFile(sourceFile)
        .setOutputDirectory(application.getTemporaryDirectory())
        .build();

    logger.debug("event sent -> " + event);
    eventManager.post(event);
  }

  private boolean tryToWriteSourceToFile(final String sourceText, final Path sourceFile) {
    try(final BufferedWriter writer = Files.newBufferedWriter(
        sourceFile,
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING)
       ) {
      writer.write(sourceText);
    } catch (IOException e) {
      final Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to compile the snippet :(", ButtonType.OK);
      alert.showAndWait();

      logger.fatal("Unable to write the source into the file!", e);
      return false;
    }

    return true;
  }

  @Subscribe
  public void onCompileFinished(final CompileFinishedEvent compileFinishedEvent) {
    logger.debug("compile finished!");
    final CompilationResult compilationResult = compileFinishedEvent.getCompilationResult();

    showCompilationNotification(compilationResult);
    putCompilationResultIntoTheTable(compilationResult);
  }

  private void putCompilationResultIntoTheTable(final CompilationResult compilationResult) {
    compilationTable.setItems(FXCollections.observableArrayList(compilationResult.getProblems()));
  }

  private void showCompilationNotification(final CompilationResult compilationResult) {
    final Notifications notification = Notifications.create();

    notification.text("Result:\n" +
        "Warnings: " + compilationResult.getWarnings().size() +
        "\nErrors: " + compilationResult.getErrors().size());

    if (compilationResult.cleanCompilation()) {
      notification.showInformation();
    } else if (compilationResult.successfulCompilation()) {
      notification.showWarning();
    } else if (compilationResult.failedCompilation()) {
      notification.showError();
    }
  }
}
