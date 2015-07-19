package com.besaba.revonline.snippetide;

import com.besaba.revonline.snippetide.api.application.IDEApplication;
import com.besaba.revonline.snippetide.api.application.IDEApplicationLauncher;
import com.besaba.revonline.snippetide.api.application.IDEInstanceContext;
import com.besaba.revonline.snippetide.api.compiler.CompilationProblem;
import com.besaba.revonline.snippetide.api.compiler.CompilationProblemType;
import com.besaba.revonline.snippetide.api.compiler.CompilationResult;
import com.besaba.revonline.snippetide.api.events.compile.CompileFinishedEvent;
import com.besaba.revonline.snippetide.api.events.compile.CompileStartEvent;
import com.besaba.revonline.snippetide.api.events.compile.CompileStartEventBuilder;
import com.besaba.revonline.snippetide.api.events.manager.EventManager;
import com.besaba.revonline.snippetide.api.events.run.MessageFromProcess;
import com.besaba.revonline.snippetide.api.events.run.RunInformationEvent;
import com.besaba.revonline.snippetide.api.events.run.RunStartEvent;
import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.plugins.Plugin;
import com.besaba.revonline.snippetide.api.plugins.PluginManager;
import com.besaba.revonline.snippetide.api.run.RunConfiguration;
import com.besaba.revonline.snippetide.configuration.contract.ConfigurationSettingsContract;
import com.besaba.revonline.snippetide.keymap.Action;
import com.besaba.revonline.snippetide.keymap.Keymap;
import com.besaba.revonline.snippetide.run.ConfigurationTab;
import com.besaba.revonline.snippetide.run.CustomPropertyEditorFactory;
import com.besaba.revonline.snippetide.api.run.RunConfigurationValues;
import com.besaba.revonline.snippetide.run.RunSnippet;
import com.besaba.revonline.snippetide.run.SimplePropertySheetItem;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.Files;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.apache.log4j.Logger;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.PropertySheet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The controller of the view ide.fxml
 */
public class IdeController {
  public static final String DEFAULT_SNIPPET_FILE_NAME = "Solution";

  private final static Logger logger = Logger.getLogger(IdeController.class);

  @FXML
  private TabPane compileAndRunPane;

  @FXML
  private TextArea runTextArea;

  @NotNull
  private String fileName = DEFAULT_SNIPPET_FILE_NAME;

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
  @FXML
  private MenuItem saveToOriginalFile;

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
  @NotNull
  private final Optional<Path> originalFile;
  @NotNull
  private Optional<RunSnippet> runSnippetThread = Optional.empty();

  /**
   * @param language What will be the language used by this view?
   */
  public IdeController(@NotNull final Language language,
                       @NotNull final Plugin plugin,
                       @Nullable final Path originalFile) {
    this.language = language;
    this.plugin = plugin;
    this.originalFile = Optional.ofNullable(originalFile);
  }

  public IdeController(@NotNull final Language language,
                       @NotNull final Plugin plugin) {
    this.language = language;
    this.plugin = plugin;
    this.originalFile = Optional.empty();
  }

  public void initialize() {
    eventManager.registerListener(this);

    prepareIde();
    prepareLanguagesList();
    prepareCompilationTable();
  }

  private void prepareIde() {
    final boolean present = originalFile.isPresent();
    saveToOriginalFile.setDisable(!present);

    if (!present) {
      prepareCodeAreaWithTemplate();
      return;
    }

    prepareCodeAreaWithOriginalFile();
  }

  private void prepareCodeAreaWithTemplate() {
    codeArea.appendText(language.getTemplate());
  }

  private void prepareCodeAreaWithOriginalFile() {
    final Path path = this.originalFile.get();

    fileName = Files.getNameWithoutExtension(path.getFileName().toString());
    loadCodeFromFile(path);
  }

  private void loadCodeFromFile(final Path file) {
    try(final BufferedReader reader = java.nio.file.Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
      codeArea.clear();

      for(String line; (line = reader.readLine()) != null; ) {
        codeArea.appendText(line);
        codeArea.appendText(System.lineSeparator());
      }
    } catch (IOException ex) {
      final Alert alert = new Alert(Alert.AlertType.WARNING, "Unable to copy the file content. The content could be incomplete", ButtonType.OK);
      alert.showAndWait();
    }
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
    final Action action = Keymap.match(keyEvent);

    if (action == null) {
      return;
    }

    switch (action) {
      case Compile: {
        compile();
        break;
      }
      case Run: {
        run();
        break;
      }
    }
  }

  @FXML
  private void run(ActionEvent event) {
    run();
  }

  private void run() {
    stopIfAlreadyRunningRunThread();
    cleanRunTextArea();

    final String sourceText = codeArea.getText();
    final Path sourceFile = Paths.get(
        application.getTemporaryDirectory().toString(),
        fileName + language.getExtensions()[0]
    );

    if (!tryToWriteSourceToFile(sourceText, sourceFile)) {
      final Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to write file content :(", ButtonType.OK);
      alert.show();
      return;
    }

    final Dialog<RunConfigurationValues> fillRunConfiguration = new Dialog<>();
    final Parent root;

    try {
      root = FXMLLoader.load(IdeController.class.getResource("fillrunconfiguration.fxml"));
    } catch (IOException e) {
      new Alert(Alert.AlertType.ERROR, "Unable to open run configuration dialog :(", ButtonType.OK).show();
      return;
    }

    final TabPane configurationsTabPane = ((TabPane) root.lookup("#configurations"));
    final RunConfiguration[] configurations = language.getRunConfigurations();

    for (final RunConfiguration configuration : configurations) {
      final Tab tab = new ConfigurationTab(configuration.getName(), configuration);
      final ObservableList<PropertySheet.Item> items = FXCollections.observableArrayList();

      configuration.getFields().forEach((name, fieldInfo) -> items.add(new SimplePropertySheetItem(name, fieldInfo)));

      final PropertySheet propertySheet = new PropertySheet(items);
      propertySheet.searchBoxVisibleProperty().set(false);
      propertySheet.setModeSwitcherVisible(false);

      propertySheet.setPropertyEditorFactory(new CustomPropertyEditorFactory());

      tab.setContent(propertySheet);

      configurationsTabPane.getTabs().add(tab);
    }

    fillRunConfiguration.getDialogPane().setContent(root);
    fillRunConfiguration.getDialogPane().getButtonTypes().add(ButtonType.OK);
    fillRunConfiguration.getDialogPane().getButtonTypes().add(new ButtonType("OK, save and reuse", ButtonBar.ButtonData.OK_DONE));
    fillRunConfiguration.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

    fillRunConfiguration.setResultConverter(param -> {
      if (param == ButtonType.CLOSE) {
        return null;
      }

      final ConfigurationTab activeTab = (ConfigurationTab) configurationsTabPane.getSelectionModel().getSelectedItem();
      final PropertySheet propertySheet = (PropertySheet) activeTab.getContent().lookup("PropertySheet");

      final Map<String, Object> values = new HashMap<>();

      propertySheet.getItems().forEach(item -> values.put(item.getName(), item.getValue()));

      final RunConfigurationValues configuration = new RunConfigurationValues(activeTab.getRunConfiguration(), values);

      if (param.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
        application.getConfiguration().set(
            ConfigurationSettingsContract.RunConfigurations.SECTION_NAME + "." +
                plugin.getPluginId() + "." +
                language.getName().hashCode(),
            configuration.getValues()
        );
      }

      return configuration;
    });

    final Optional<RunConfigurationValues> runConfigurationValues = fillRunConfiguration.showAndWait();

    runConfigurationValues.ifPresent(value ->
        eventManager.post(new RunStartEvent(language, sourceFile, application.getTemporaryDirectory(), value)));
  }

  private void cleanRunTextArea() {
    runTextArea.clear();
  }

  // <editor-fold name="Compile events">
  @FXML
  private void compile(ActionEvent actionEvent) {
    compile();
  }

  private void compile() {
    logger.info("Pressed compile key");

    // if the code is too big what will happen?
    final Path sourceFile = Paths.get(
        application.getTemporaryDirectory().toString(),
        fileName + language.getExtensions()[0]
    );

    if (!tryToWriteSourceToFile(sourceFile)) {
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
    try(final BufferedWriter writer = java.nio.file.Files.newBufferedWriter(
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

    compileAndRunPane.getSelectionModel().select(0);
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

  // </editor-fold>

  public void saveToOriginalPath(ActionEvent actionEvent) {
    if (!originalFile.isPresent()) {
      return;
    }

    if (!tryToWriteSourceToFile(originalFile.get())) {
      final Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to save content to the file :(", ButtonType.OK);
      alert.show();
    }
  }

  public void saveFileCopy(ActionEvent actionEvent) {
    final FileChooser fileChooser = new FileChooser();

    final String[] fixedExtensions = Arrays.stream(language.getExtensions()).map(extension -> "*" + extension).toArray(String[]::new);
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter(language.getName(), fixedExtensions)
    );

    final File tempFile = fileChooser.showSaveDialog(null);

    if (tempFile == null) {
      return;
    }

    final Path destination = tempFile.toPath();

    if (!tryToWriteSourceToFile(destination)) {
      final Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to save content to the file :(", ButtonType.OK);
      alert.show();
    }
  }

  public void closeApplication(ActionEvent actionEvent) {
    Platform.exit();
  }

  public void newFile(ActionEvent actionEvent) {
    openAnotherInstance(null);
  }

  public void openFile(ActionEvent actionEvent) {
    final FileChooser fileChooser = new FileChooser();

    final String[] fixedExtensions = Arrays.stream(language.getExtensions()).map(extension -> "*" + extension).toArray(String[]::new);
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(language.getName(), fixedExtensions));
    final File tempFile = fileChooser.showOpenDialog(null);

    if (tempFile == null) {
      return;
    }

    final Path openFile = tempFile.toPath();
    openAnotherInstance(openFile);
  }

  private void openAnotherInstance(@Nullable final Path fileToOpen) {
    final IDEInstanceContext ideInstanceContext = new IDEInstanceContext(language, plugin, fileToOpen);
    application.openIdeInstance(ideInstanceContext);
  }

  @Subscribe
  public void runInformationResponse(final RunInformationEvent runInformationEvent) {
    compileAndRunPane.getSelectionModel().select(1);

    stopIfAlreadyRunningRunThread();

    final RunSnippet runSnippet = new RunSnippet(runInformationEvent, eventManager);
    runSnippet.start();
  }

  @Subscribe
  public void onMessageFromSubprocess(final MessageFromProcess messageFromProcess) {
    runTextArea.appendText(messageFromProcess.getMessage());
    runTextArea.appendText(System.lineSeparator());
  }

  private void stopIfAlreadyRunningRunThread() {
    if (!runSnippetThread.isPresent()) {
      return;
    }

    runSnippetThread.get().stop();
    runSnippetThread = Optional.empty();
  }

  public void showLogs(ActionEvent actionEvent) {
    try {
      Desktop.getDesktop().open(new File(System.getProperty("user.dir"), "SnippetIDE" + "\\logs.txt"));
    } catch (IOException|IllegalArgumentException e) {
      final Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to open log file", ButtonType.OK);
      alert.show();
      logger.fatal("Unable to open log file", e);
    }
  }

  public void showPluginsList(ActionEvent actionEvent) {
    application.openPluginsList(runTextArea.getScene().getWindow());
  }

  public void showAbout(final ActionEvent actionEvent) {
    application.openAboutWindow(runTextArea.getScene().getWindow());
  }

  public void showKeymapUi(ActionEvent actionEvent) {
    try {
      application.openKeymapSetting(runTextArea.getScene().getWindow());
    } catch (IOException e) {
      logger.fatal("unable to keymap settings", e);
      new Alert(Alert.AlertType.ERROR, "Unable to keymap settings page", ButtonType.OK).show();
    }
  }
}
