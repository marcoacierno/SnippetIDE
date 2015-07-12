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
import com.besaba.revonline.snippetide.api.plugins.Plugin;
import com.besaba.revonline.snippetide.api.plugins.PluginManager;
import com.google.common.eventbus.Subscribe;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
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
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The controller of the view ide.fxml
 */
public class IdeController {
  public static final String DEFAULT_SNIPPET_FILE_NAME = "Solution";

  private final static Logger logger = Logger.getLogger(IdeController.class);

  @NotNull
  private Language language;
  @NotNull
  private Plugin plugin;

  @FXML
  private ComboBox<PluginLanguage> languagesChoice;
  @NotNull
  private final IDEApplication application = IDEApplicationLauncher.getIDEApplication();
  @NotNull
  private final EventManager eventManager = application.getEventManager();
  @NotNull
  private final PluginManager pluginManager = application.getPluginManager();
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
  public IdeController(@NotNull final Language language,
                       @NotNull final Plugin plugin) {
    this.language = language;
    this.plugin = plugin;
  }

  public void initialize() {
    eventManager.registerListener(this);

    prepareLanguagesList();
    prepareCompilationTable();
  }

  private void prepareLanguagesList() {
    languagesChoice.setCellFactory(param -> new ListCell<PluginLanguage>() {
      @Override
      protected void updateItem(final PluginLanguage item, final boolean empty) {
        super.updateItem(item, empty);

        if (!empty) {
          setText(item.getLanguage().getName() + " from " + item.getPlugin().getName());
        } else {
          setText(null);
        }
      }
    });

    languagesChoice.setConverter(new StringConverter<PluginLanguage>() {
      @Override
      public String toString(final PluginLanguage object) {
        return object.getLanguage().getName() + " from " + object.getPlugin().getName();
      }

      @Override
      public PluginLanguage fromString(final String string) {
        return null;
      }
    });

    languagesChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      language = newValue.getLanguage();
      plugin = newValue.getPlugin();
    });

    pluginManager.getPlugins().forEach(plugin -> {
      plugin.getLanguages().forEach(pluginLanguage -> {
        languagesChoice.getItems().add(new PluginLanguage(pluginLanguage, plugin));
      });
    });

    languagesChoice.setValue(new PluginLanguage(language, plugin));
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
        DEFAULT_SNIPPET_FILE_NAME + language.getExtensions()[0]
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

  private boolean tryToWriteSourceToFile(final Path destination) {
    return tryToWriteSourceToFile(codeArea.getText(), destination);
  }

  private boolean tryToWriteSourceToFile(final String source, final Path destination) {
    try(final BufferedWriter writer = Files.newBufferedWriter(
        destination,
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING)
       ) {
      writer.write(source);
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

  public void saveFileCopy(ActionEvent actionEvent) {
    final FileChooser fileChooser = new FileChooser();

    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter(language.getName(), language.getExtensions())
    );

    final Path destination = fileChooser.showSaveDialog(null).toPath();

    if (!tryToWriteSourceToFile(destination)) {
      final Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to save content to the file :(", ButtonType.OK);
      alert.show();
    }
  }

  private static class PluginLanguage {
    private Language language;
    private Plugin plugin;

    public PluginLanguage(final Language language, final Plugin plugin) {
      this.language = language;
      this.plugin = plugin;
    }

    public Language getLanguage() {
      return language;
    }

    public Plugin getPlugin() {
      return plugin;
    }
  }
}
