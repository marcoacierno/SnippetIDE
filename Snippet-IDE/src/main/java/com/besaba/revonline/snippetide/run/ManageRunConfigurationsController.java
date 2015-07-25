package com.besaba.revonline.snippetide.run;

import com.besaba.revonline.snippetide.api.application.IDEApplication;
import com.besaba.revonline.snippetide.api.application.IDEApplicationLauncher;
import com.besaba.revonline.snippetide.api.configuration.Configuration;
import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.plugins.Plugin;
import com.besaba.revonline.snippetide.api.run.ManageRunConfigurationsContext;
import com.besaba.revonline.snippetide.api.datashare.StructureDataContainer;
import com.besaba.revonline.snippetide.api.datashare.DataContainer;
import com.besaba.revonline.snippetide.configuration.contract.ConfigurationSettingsContract;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
  private final String languageConfigurationsQuery;

  private static final Logger logger = Logger.getLogger(ManageRunConfigurationsController.class);

  public ManageRunConfigurationsController(final @NotNull ManageRunConfigurationsContext context) {
    this.context = context;

    plugin = context.getPlugin();
    language = context.getLanguage();

    languageConfigurationsQuery
        = ConfigurationSettingsContract.RunConfigurations.generateRunConfigurationsLanguageQuery(plugin, language);
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
          .getDataContainer()
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
        changeDefaultListener(observable, oldValue, newValue, value.getValue().getDataContainer().getParentId());
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
      applicationConfiguration.set(languageConfigurationsQuery + ".default", parentId);
    } else {
      applicationConfiguration.set(languageConfigurationsQuery + ".default", -1);
    }
  }

  private void addStoredConfigurationsToTable() {
    try {
      logger.debug("try to get default");
      logger.debug("query -> " + languageConfigurationsQuery + ".default");
      final int defaultConfiguration = applicationConfiguration.getAsInt(
          languageConfigurationsQuery + ".default"
      ).orElse(-1);

      for (final StructureDataContainer configuration : language.getRunConfigurations()) {
        logger.debug("checking " + configuration.getName());
        logger.debug("query -> " + (languageConfigurationsQuery + "." + configuration.getId()));

        final Optional<Map<String, Object>> values =
            applicationConfiguration.get(languageConfigurationsQuery + "." + configuration.getId()
        );

        if (!values.isPresent()) {
          continue;
        }

        final DataContainer dataContainer = new DataContainer(configuration.getId(), values.get());
        final RunConfigurationValuesManagerData runConfigurationValuesManagerData
            = new RunConfigurationValuesManagerData(
            dataContainer,
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

  public void deleteConfiguration(ActionEvent actionEvent) {
    final ObservableList<RunConfigurationValuesManagerData> selectedItems
        = runConfigurationsTable.getSelectionModel().getSelectedItems();
    final int countElementsSelected = selectedItems.size();

    if (countElementsSelected == 0) {
      return;
    }

    final Optional<ButtonType> response = new Alert(
        Alert.AlertType.CONFIRMATION,
        "Are you sure you want to remove " + countElementsSelected + " configuration(s)?",
        ButtonType.YES, ButtonType.NO
    ).showAndWait();

    response.ifPresent(button -> {
      if (button == ButtonType.NO) {
        return;
      }

      for (final RunConfigurationValuesManagerData item : selectedItems) {
        boolean removed = applicationConfiguration.remove(
            languageConfigurationsQuery + "." + item.getDataContainer().getParentId()
        );

        if (item.isDefault()) {
          removed &= applicationConfiguration.remove(languageConfigurationsQuery + ".default");
        }

        if (!removed) {
          new Alert(
              Alert.AlertType.INFORMATION,
              "Ops, failed to remove: " + item.getConfigurationName(),
              ButtonType.OK
          ).show();
        } else {
          runConfigurationsTable.getItems().remove(item);
        }
      }
    });
  }
}
