package com.besaba.revonline.snippetide.run;

import com.besaba.revonline.snippetide.api.application.IDEApplication;
import com.besaba.revonline.snippetide.api.application.IDEApplicationLauncher;
import com.besaba.revonline.snippetide.api.configuration.Configuration;
import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.plugins.Plugin;
import com.besaba.revonline.snippetide.api.run.ManageRunConfigurationsContext;
import com.besaba.revonline.snippetide.api.run.RunConfiguration;
import com.besaba.revonline.snippetide.api.run.RunConfigurationValues;
import com.besaba.revonline.snippetide.configuration.contract.ConfigurationSettingsContract;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public class ManageRunConfigurationsController {
  @FXML
  private TableView<RunConfigurationValuesManagerData> runConfigurationsTable;
  @FXML
  private TableColumn<RunConfigurationValuesManagerData, String> configurationName;
  @FXML
  private TableColumn<RunConfigurationValuesManagerData, String> configurationValues;
  @FXML
  private TableColumn<RunConfigurationValuesManagerData, Boolean> configurationAsDefault;
  @NotNull
  private final ManageRunConfigurationsContext context;
  @NotNull
  private final IDEApplication application = IDEApplicationLauncher.getIDEApplication();
  @NotNull
  private final Configuration applicationConfiguration = application.getConfiguration();
  @NotNull
  private final Plugin plugin;
  @NotNull
  private final Language language;
  @NotNull
  private final String pluginConfigurationKey;
  private static final Logger logger = Logger.getLogger(ManageRunConfigurationsController.class);

  public ManageRunConfigurationsController(final @NotNull ManageRunConfigurationsContext context) {
    this.context = context;

    plugin = context.getPlugin();
    language = context.getLanguage();

    pluginConfigurationKey =
        ConfigurationSettingsContract.RunConfigurations.SECTION_NAME + "." + plugin.getPluginId();
  }

  public void initialize() {
    prepareCellViews();
    addStoredConfigurationsToTable();
  }

  private void prepareCellViews() {
    runConfigurationsTable.setEditable(true);

    configurationName.setEditable(false);
    configurationName.setCellValueFactory(value -> new ReadOnlyStringWrapper(
        String.valueOf(value.getValue().getConfigurationName()))
    );

    configurationValues.setEditable(false);
    configurationValues.setCellValueFactory(value -> new ReadOnlyStringWrapper(
        value
          .getValue()
          .getRunConfigurationValues()
          .getValues()
          .entrySet()
          .stream()
          .map(entry -> entry.getKey() + ": " + entry.getValue())
          .reduce("", (acc, next) -> acc + next + "\n")
    ));

    configurationAsDefault.setEditable(true);
    configurationAsDefault.setCellValueFactory(value -> {
      final SimpleBooleanProperty defaultProperty = new SimpleBooleanProperty(value.getValue().isDefault());
      defaultProperty.addListener(((observable, oldValue, newValue) -> {
        changeDefaultListener(observable, oldValue, newValue, value.getValue().getRunConfigurationValues().getParentId());
      }));
      return defaultProperty;
    });
    configurationAsDefault.setCellFactory(param -> new CheckBoxTableCell<>());
  }

  private void changeDefaultListener(final ObservableValue<? extends Boolean> observableValue,
                                     final Boolean oldValue,
                                     final Boolean newValue,
                                     final int parentId) {
    if (newValue) {
      applicationConfiguration.set(pluginConfigurationKey + "." + language.getName().hashCode() + ".default", parentId);
    } else {
      applicationConfiguration.set(pluginConfigurationKey + "." + language.getName().hashCode() + ".default", -1);
    }
  }

  private void addStoredConfigurationsToTable() {
    try {
      logger.debug("try to get default");
      logger.debug("query -> " + pluginConfigurationKey + "." + language.hashCode() + ".default");
      final int defaultConfiguration = applicationConfiguration.getAsInt(
          pluginConfigurationKey + "." + language.getName().hashCode() + ".default"
      ).orElse(-1);

      for (final RunConfiguration configuration : language.getRunConfigurations()) {
        logger.debug("checking " + configuration.getName());
        logger.debug("query -> " + (pluginConfigurationKey + "." + language.getName().hashCode() + "." + configuration.getId()));

        final Optional<Map<String, Object>> values =
            applicationConfiguration.get(pluginConfigurationKey + "." + language.getName().hashCode() + "." + configuration.getId()
            );

        if (!values.isPresent()) {
          continue;
        }

        final RunConfigurationValues runConfigurationValues = new RunConfigurationValues(configuration.getId(), values.get());
        final RunConfigurationValuesManagerData runConfigurationValuesManagerData
            = new RunConfigurationValuesManagerData(
              runConfigurationValues,
              configuration.getName(),
              defaultConfiguration == configuration.getId()
          );
        runConfigurationsTable.getItems().add(runConfigurationValuesManagerData);
      }
    } catch (IllegalArgumentException e) {
      /* no configurations to show */
      logger.info("no configurations to show", e);
    }
  }
}
