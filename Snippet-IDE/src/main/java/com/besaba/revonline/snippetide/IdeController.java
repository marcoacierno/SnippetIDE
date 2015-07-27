package com.besaba.revonline.snippetide;

import com.besaba.revonline.snippetide.api.application.IDEApplication;
import com.besaba.revonline.snippetide.api.application.IDEApplicationLauncher;
import com.besaba.revonline.snippetide.api.application.IDEInstanceContext;
import com.besaba.revonline.snippetide.api.compiler.CompilationProblem;
import com.besaba.revonline.snippetide.api.compiler.CompilationProblemType;
import com.besaba.revonline.snippetide.api.compiler.CompilationResult;
import com.besaba.revonline.snippetide.api.datashare.DataContainer;
import com.besaba.revonline.snippetide.api.events.boot.UnBootEvent;
import com.besaba.revonline.snippetide.api.events.compile.CompileFinishedEvent;
import com.besaba.revonline.snippetide.api.events.compile.CompileStartEvent;
import com.besaba.revonline.snippetide.api.events.compile.CompileStartEventBuilder;
import com.besaba.revonline.snippetide.api.events.manager.EventManager;
import com.besaba.revonline.snippetide.api.events.run.MessageFromProcess;
import com.besaba.revonline.snippetide.api.events.run.RunInformationEvent;
import com.besaba.revonline.snippetide.api.events.run.RunStartEvent;
import com.besaba.revonline.snippetide.api.events.run.SendMessageToProcessEvent;
import com.besaba.revonline.snippetide.api.events.share.ShareCompletedEvent;
import com.besaba.revonline.snippetide.api.events.share.ShareFailedEvent;
import com.besaba.revonline.snippetide.api.events.share.ShareRequestEvent;
import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.plugins.Plugin;
import com.besaba.revonline.snippetide.api.plugins.PluginManager;
import com.besaba.revonline.snippetide.api.run.ManageRunConfigurationsContext;
import com.besaba.revonline.snippetide.datashare.DataStructureManager;
import com.besaba.revonline.snippetide.datashare.context.DataStructureManagerContext;
import com.besaba.revonline.snippetide.datashare.context.RunConfigurationDataStructureManagerContext;
import com.besaba.revonline.snippetide.datashare.context.ShareServiceParametersDataStructureManagerContext;
import com.besaba.revonline.snippetide.keymap.Action;
import com.besaba.revonline.snippetide.keymap.Keymap;
import com.besaba.revonline.snippetide.run.RunSnippet;
import com.besaba.revonline.snippetide.ui.CodeTextArea;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.Files;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.log4j.Logger;
import org.controlsfx.control.Notifications;
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
import java.util.Optional;

/**
 * The controller of the view ide.fxml
 */
public class IdeController {
  public static final String DEFAULT_SNIPPET_FILE_NAME = "Solution";

  private final static Logger logger = Logger.getLogger(IdeController.class);

  @FXML
  private Menu shareOnMenu;
  @FXML
  private TextField inputField;
  @FXML
  private Button manageRunConfigurations;
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
  private CodeTextArea codeArea;
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
  private DataStructureManagerContext runconfigurationContext;
  private boolean dirtyCodeArea = false;
  @NotNull
  private final Stage stage;

  /**
   * @param language What will be the language used by this view?
   * @param stage
   */
  public IdeController(@NotNull final Language language,
                       @NotNull final Plugin plugin,
                       @Nullable final Path originalFile, final @NotNull Stage stage) {
    initUnbootWorker();
    this.stage = stage;
    changeLanguage(plugin, language);
    this.originalFile = Optional.ofNullable(originalFile);
  }

  public IdeController(@NotNull final Language language,
                       @NotNull final Plugin plugin, final @NotNull Stage stage) {
    initUnbootWorker();
    this.stage = stage;
    changeLanguage(plugin, language);
    this.originalFile = Optional.empty();
  }

  private void initUnbootWorker() {
    new UnBootWorker();
  }

  public void initialize() {
    eventManager.registerListener(this);

    prepareRunAndCompileKeysListener();
    prepareIde();
    prepareShareOnMenu();
    prepareLanguagesList();
    prepareCompilationTable();
  }

  private void prepareRunAndCompileKeysListener() {
    stage.addEventFilter(KeyEvent.KEY_PRESSED, this::compileOrRunKeyPress);
  }

  private void prepareShareOnMenu() {
    pluginManager.getPlugins().forEach(p -> {
      p.getShareServices().forEach(service -> {
        final MenuItem menuItem = new MenuItem(service.getServiceName());

        menuItem.setOnAction(event -> {
          final ShareServiceParametersDataStructureManagerContext context = new ShareServiceParametersDataStructureManagerContext(p, service);
          final Optional<DataContainer> dataContainer = new DataStructureManager(context).getDataContainer();

          dataContainer.ifPresent(container -> {
            eventManager.post(new ShareRequestEvent(service, fileName, codeArea.getText(), language, container));
          });
        });
        menuItem.setGraphic(new ImageView(service.getImage()));

        shareOnMenu.getItems().add(menuItem);
      });
    });
  }

  private void prepareIde() {
    final boolean present = originalFile.isPresent();
    saveToOriginalFile.setDisable(!present);
    manageRunConfigurations.setText("Manage run configurations for " + language.getName());

    if (!present) {
      prepareCodeAreaWithTemplate();
    } else {
      prepareCodeAreaWithOriginalFile();
      dirtyCodeArea = true;
    }

    codeArea.setOnKeyTyped(event -> dirtyCodeArea = true);
    inputField.setOnKeyPressed(this::onInputSubmit);
  }

  private void onInputSubmit(final KeyEvent keyEvent) {
    if (!runSnippetThread.isPresent()) {
      return;
    }

    if (keyEvent.getCode() != KeyCode.ENTER) {
      logger.debug("pressed something else");
      return;
    }

    logger.debug("pressing send SEND IT!");

    runSnippetThread.ifPresent(runSnippet -> {
      final String messageToSend = inputField.getText();
      runTextArea.appendText(messageToSend);
      runTextArea.appendText(System.lineSeparator());
      eventManager.post(new SendMessageToProcessEvent(messageToSend));
      inputField.clear();
    });
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
      changeLanguage(newValue.getPlugin(), newValue.getLanguage());
    });

    pluginManager.getPlugins().forEach(plugin -> {
      plugin.getLanguages().forEach(pluginLanguage -> {
        languagesChoice.getItems().add(new PluginLanguage(pluginLanguage, plugin));
      });
    });

    languagesChoice.setValue(new PluginLanguage(language, plugin));
  }

  private void changeLanguage(@NotNull final Plugin plugin,
                              @NotNull final Language language) {
    this.plugin = plugin;
    this.language = language;
    this.runconfigurationContext = new RunConfigurationDataStructureManagerContext(plugin, language);

    // update text only if available, since this method is excepted to be called from constructor too
    if (manageRunConfigurations != null) {
      manageRunConfigurations.setText("Manage run configurations for " + language.getName());
    }

    if (!dirtyCodeArea && codeArea != null) {
      codeArea.setText(language.getTemplate());
    }
  }

  private void prepareCompilationTable() {
    compilationTableType.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getType()));
    compilationTableLine.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getLine()));
    compilationTableMessage.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getMessage()));
  }

  public void compileOrRunKeyPress(Event event) {
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

    final Optional<DataContainer> dataContainer = new DataStructureManager(runconfigurationContext)
        .getDataContainer();

    dataContainer.ifPresent(container -> {
      eventManager.post(new RunStartEvent(language, sourceFile, application.getTemporaryDirectory(), container));
    });
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

    if (!runInformationEvent.needExternalProcess()) {
      return;
    }

    final RunSnippet runSnippet = new RunSnippet(runInformationEvent, eventManager);
    runSnippetThread = Optional.of(runSnippet);
    runSnippet.start();
  }

  @Subscribe
  public void onMessageFromSubprocess(final MessageFromProcess messageFromProcess) {
    Platform.runLater(() -> {
      runTextArea.appendText(messageFromProcess.getMessage());
      runTextArea.appendText(System.lineSeparator());
    });
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
    try {
      application.openPluginsList(runTextArea.getScene().getWindow());
    } catch (IOException e) {
      new Alert(Alert.AlertType.ERROR, "Unable to open plugins list", ButtonType.OK).show();
      logger.error("Failed to open plugins list stage", e);
    }
  }

  public void showAbout(final ActionEvent actionEvent) {
    try {
      application.openAboutWindow(runTextArea.getScene().getWindow());
    } catch (IOException e) {
      new Alert(Alert.AlertType.ERROR, "Unable to open about window", ButtonType.OK).show();
      logger.error("Failed to open about window", e);
    }
  }

  public void showKeymapUi(ActionEvent actionEvent) {
    try {
      application.openKeymapSetting(runTextArea.getScene().getWindow());
    } catch (IOException e) {
      logger.fatal("unable to keymap settings", e);
      new Alert(Alert.AlertType.ERROR, "Unable to keymap settings page", ButtonType.OK).show();
    }
  }

  @FXML
  private void openManageConfigurations(ActionEvent actionEvent) {
    try {
      final ManageRunConfigurationsContext context = new ManageRunConfigurationsContext(plugin, language);
      application.openManageConfigurations(context, runTextArea.getScene().getWindow());
    } catch (IOException e) {
      logger.fatal("unable to keymap settings", e);
      new Alert(Alert.AlertType.ERROR, "Unable to open manage configurations page", ButtonType.OK).show();
    }
  }

  public void stopRunSnippetThread(ActionEvent actionEvent) {
    stopIfAlreadyRunningRunThread();
  }

  @Subscribe
  public void onSuccessfulShare(@NotNull final ShareCompletedEvent event) {
    Platform.runLater(() -> {
      final Optional<ButtonType> buttonType
          = new Alert(Alert.AlertType.INFORMATION,
          "Shared: " + event.getData(), ButtonType.OK,
          new ButtonType("Copy", ButtonBar.ButtonData.APPLY)
      ).showAndWait();

      buttonType.ifPresent(button -> {
        if (button.getButtonData() == ButtonBar.ButtonData.APPLY) {
          final Clipboard clipboard = Clipboard.getSystemClipboard();
          final ClipboardContent content = new ClipboardContent();
          content.putString(event.getData());
          clipboard.setContent(content);
        }
      });
    });
  }

  @Subscribe
  public void onFailedShare(@NotNull final ShareFailedEvent event) {
    Platform.runLater(() -> {
      new Alert(Alert.AlertType.ERROR, "Unable to share your code! :( " + event.getReason(), ButtonType.OK).show();
      logger.error("share failed", event.getThrowable());
    });
  }

  public class UnBootWorker {
    public UnBootWorker() {
      logger.debug("unboot worker, register");
      eventManager.registerListener(this);
    }

    @Subscribe
    public void unbootEvent(final UnBootEvent unBootEvent) {
      logger.debug("unboot event");
      runSnippetThread.ifPresent(RunSnippet::stop);
      runSnippetThread = Optional.empty();
    }
  }
}
