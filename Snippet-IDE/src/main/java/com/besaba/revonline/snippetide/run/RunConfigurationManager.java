package com.besaba.revonline.snippetide.run;

import com.besaba.revonline.snippetide.IdeController;
import com.besaba.revonline.snippetide.api.application.IDEApplication;
import com.besaba.revonline.snippetide.api.application.IDEApplicationLauncher;
import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.plugins.Plugin;
import com.besaba.revonline.snippetide.api.run.FieldInfo;
import com.besaba.revonline.snippetide.api.run.RunConfiguration;
import com.besaba.revonline.snippetide.api.run.RunConfigurationValues;
import com.besaba.revonline.snippetide.configuration.contract.ConfigurationSettingsContract;
import com.besaba.revonline.snippetide.converter.Converters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.apache.log4j.Logger;
import org.controlsfx.control.PropertySheet;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RunConfigurationManager {
  private final static Logger logger = Logger.getLogger(RunConfigurationManager.class);
  private final static IDEApplication application = IDEApplicationLauncher.getIDEApplication();

  @NotNull
  private final Language language;
  @NotNull
  private final Plugin plugin;

  public RunConfigurationManager(@NotNull final Language language, @NotNull final Plugin plugin) {
    this.language = language;
    this.plugin = plugin;
  }

  public Optional<RunConfigurationValues> getRunConfiguration() {
    return Optional.ofNullable(
        tryToFindSavedRunConfiguration()
            .orElseGet(() -> showRunConfigurationDialog()
                    .orElseGet(() -> null)
            )
    );
  }

  private Optional<RunConfigurationValues> tryToFindSavedRunConfiguration() {
    int defaultConfiguration = -1;

    try {
      defaultConfiguration = application.getConfiguration().getAsInt(
          ConfigurationSettingsContract.RunConfigurations.generateLanguageDefaultRunConfigurationQuery(plugin, language)
      ).orElse(-1);
    } catch (IllegalArgumentException e) {
      logger.info("unable to access debug property", e);
    }

    if (defaultConfiguration != -1) {
      final Optional<RunConfigurationValues> runConfigurationValues = tryToLoadSpecificConfiguration(defaultConfiguration);

      if (runConfigurationValues.isPresent()) {
        return runConfigurationValues;
      }
    }

    try {
      final Optional<RunConfigurationValues> values = tryAllStoredConfigurations();

      if (values.isPresent()) {
        return values;
      }

    } catch (IllegalArgumentException e) {
      logger.debug("stop reading combination", e);
    }

    return Optional.empty();
  }

  @NotNull
  private Optional<RunConfigurationValues> tryAllStoredConfigurations() {
    for (final RunConfiguration configuration : language.getRunConfigurations()) {
      final Optional<RunConfigurationValues> values = tryToLoadSpecificConfiguration(configuration.getId());

      if (values.isPresent()) {
        return values;
      }
    }

    return Optional.empty();
  }

  private Optional<RunConfigurationValues> tryToLoadSpecificConfiguration(final int configurationId) {
    try {
      final Optional<Map<String, Object>> values = application.getConfiguration().get(
          ConfigurationSettingsContract.RunConfigurations.generateRunConfigurationsLanguageQuery(plugin, language)
            + "." + configurationId
      );

      if (values.isPresent()) {
        RunConfiguration originalConfiguration = null;

        for (final RunConfiguration runConfiguration : language.getRunConfigurations()) {
          if (runConfiguration.getId() == configurationId) {
            originalConfiguration = runConfiguration;
          }
        }

        if (originalConfiguration == null) {
          throw new AssertionError("originalConfiguration cannot be null here");
        }

        return Optional.of(new RunConfigurationValues(configurationId, tryToFixValues(values.get(), originalConfiguration)));
      }
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }

    return Optional.empty();
  }

  private Map<String, Object> tryToFixValues(final Map<String, Object> restoredValues, final RunConfiguration originalConfiguration) {
    final Map<String, FieldInfo> originalRun = originalConfiguration.getFields();
    final Map<String, Object> fixedValues = new HashMap<>();

    restoredValues.forEach((key, value) -> {
      final FieldInfo keyInfo = originalRun.get(key);
      final Class<?> destinationType = keyInfo.getType();
      // source type is always string
      final String valueString = value.toString();
      final Converters converters = new Converters();
      final Object fixedValue = converters.convert(String.class, destinationType, valueString);
      fixedValues.put(key, fixedValue);
    });

    return fixedValues;
  }

  @NotNull
  private Optional<RunConfigurationValues> showRunConfigurationDialog() {
    final RunConfiguration[] configurations = language.getRunConfigurations();

    if (configurations.length == 0) {
      return Optional.of(
          new RunConfigurationValues(-1, Collections.<String, Object>emptyMap())
      );
    }

    final Dialog<RunConfigurationValues> fillRunConfiguration = new Dialog<>();
    final Parent root;

    try {
      root = FXMLLoader.load(IdeController.class.getResource("runconfigurations/fillrunconfiguration.fxml"));
    } catch (IOException e) {
      new Alert(Alert.AlertType.ERROR, "Unable to open run configuration dialog :(", ButtonType.OK).show();
      return Optional.empty();
    }

    final TabPane configurationsTabPane = ((TabPane) root.lookup("#configurations"));

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
    fillRunConfiguration.getDialogPane().getButtonTypes().add(new ButtonType("OK, save and reuse", ButtonBar.ButtonData.APPLY));
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

      if (param.getButtonData() == ButtonBar.ButtonData.APPLY) {
        application.getConfiguration().set(
            ConfigurationSettingsContract.RunConfigurations.generateRunConfigurationsLanguageQuery(plugin, language) + "." +
                configuration.getParentId(),
            configuration.getValues()
        );
      }

      return configuration;
    });

    return fillRunConfiguration.showAndWait();
  }
}