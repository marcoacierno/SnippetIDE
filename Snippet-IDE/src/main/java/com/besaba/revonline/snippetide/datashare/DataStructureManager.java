package com.besaba.revonline.snippetide.datashare;

import com.besaba.revonline.snippetide.IdeController;
import com.besaba.revonline.snippetide.api.application.IDEApplicationLauncher;
import com.besaba.revonline.snippetide.api.configuration.Configuration;
import com.besaba.revonline.snippetide.api.datashare.DataContainer;
import com.besaba.revonline.snippetide.api.datashare.StructureDataContainer;
import com.besaba.revonline.snippetide.api.datashare.StructureFieldInfo;
import com.besaba.revonline.snippetide.converter.Converters;
import com.besaba.revonline.snippetide.datashare.context.DataStructureManagerContext;
import com.besaba.revonline.snippetide.propertyeditor.CustomPropertyEditorFactory;
import com.besaba.revonline.snippetide.run.SimplePropertySheetItem;
import com.google.common.collect.ImmutableMap;
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
import javafx.util.Callback;
import org.apache.log4j.Logger;
import org.controlsfx.control.PropertySheet;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

/**
 * Used to read a datastructure.
 *
 * <p>It can be restored from a saved setting
 * if exists, or it will show a dialog
 * to the user asking him to insert the values.</p>
 */
public class DataStructureManager {
  private final static Logger logger = Logger.getLogger(DataStructureManager.class);

  @NotNull
  private final Configuration configuration = IDEApplicationLauncher.getIDEApplication().getConfiguration();
  @NotNull
  private final DataStructureManagerContext context;

  public DataStructureManager(@NotNull final DataStructureManagerContext context) {
    this.context = context;
  }

  /**
   * Tries to load the default data container or asks to the user
   * to create one.
   *
   * @return The datastructures with the values provided by the user.
   */
  public Optional<DataContainer> getDataContainer() {
    return Optional.ofNullable(
        tryToLoadDefaultDataContainer().orElseGet(
            () -> askToTheUserToCreateADataContainer()
              .orElseGet(() -> null)
        )
    );
  }

  public Optional<DataContainer> createDataContainer() {
    return Optional.of(askToTheUserToCreateADataContainer().orElseGet(() -> null));
  }

  @NotNull
  private Optional<DataContainer> askToTheUserToCreateADataContainer() {
    final StructureDataContainer[] structures = context.getDataContainerStructures();

    if (structures.length == 0) {
      // has no structures, so we just return an empty map without a parent
      return Optional.of(new DataContainer(-1, Collections.emptyMap()));
    }

    final Dialog<DataContainer> fillRunConfiguration = new Dialog<>();
    fillRunConfiguration.setTitle("Data");

    final Parent root;

    try {
      root = FXMLLoader.load(IdeController.class.getResource("structure/fillcontainer.fxml"));
    } catch (IOException e) {
      new Alert(Alert.AlertType.ERROR, "Unable to open run configuration dialog :(", ButtonType.OK).show();
      return Optional.empty();
    }

    final TabPane structuresTabPane = ((TabPane) root.lookup("#configurations"));

    for (final StructureDataContainer structure : structures) {
      final Tab tab = new DataStructureTab(structure.getName(), structure);
      final ObservableList<PropertySheet.Item> items = FXCollections.observableArrayList();

      structure.getFields().forEach((name, fieldInfo) -> items.add(new SimplePropertySheetItem(name, fieldInfo)));

      final PropertySheet propertySheet = new PropertySheet(items);
      propertySheet.searchBoxVisibleProperty().set(false);
      propertySheet.setModeSwitcherVisible(false);

      propertySheet.setPropertyEditorFactory(new CustomPropertyEditorFactory());

      tab.setContent(propertySheet);

      structuresTabPane.getTabs().add(tab);
    }

    fillRunConfiguration.getDialogPane().setContent(root);
    fillRunConfiguration.getDialogPane().getButtonTypes().add(ButtonType.OK);
    fillRunConfiguration.getDialogPane().getButtonTypes().add(new ButtonType("Use and save", ButtonBar.ButtonData.APPLY));
    fillRunConfiguration.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

    fillRunConfiguration.setResultConverter(param -> {
      if (param == ButtonType.CLOSE) {
        return null;
      }

      final DataStructureTab activeTab = (DataStructureTab) structuresTabPane.getSelectionModel().getSelectedItem();
      final PropertySheet propertySheet = (PropertySheet) activeTab.getContent().lookup("PropertySheet");

      final boolean allFieldsCorrect = propertySheet
          .getItems()
          .stream()
          .anyMatch(item -> ((SimplePropertySheetItem) item).check(item.getValue()));

      if (!allFieldsCorrect) {
        return null;
      }

      final Map<String, Object> values = propertySheet
          .getItems()
          .stream()
          .collect(Collectors.toMap(PropertySheet.Item::getName, PropertySheet.Item::getValue));

      final DataContainer container = new DataContainer(activeTab.getStructureDataContainer(), values);

      if (param.getButtonData() == ButtonBar.ButtonData.APPLY) {
        configuration.set(context.getDataContainerConfigurationNode(container.getParentId()), container.getValues());
        configuration.set(context.getDefaultDataContainerConfigurationNode(), container.getParentId());
      }

      return container;
    });

    return fillRunConfiguration.showAndWait();
  }

  @NotNull
  private Optional<DataContainer> tryToLoadDefaultDataContainer() {
    final int defaultDataContainer = configuration.getAsInt(context.getDefaultDataContainerConfigurationNode()).orElse(-1);

    if (defaultDataContainer == -1) {
      return Optional.empty();
    }

    return tryToLoadSpecificDataContainer(defaultDataContainer);
  }

  @NotNull
  private Optional<DataContainer> tryToLoadSpecificDataContainer(final int defaultDataContainer) {
    final Optional<Map<String, Object>> values
        = configuration.get(context.getDataContainerConfigurationNode(defaultDataContainer));

    if (values.isPresent()) {
      final Map<String, Object> fixedValues;

      try {
        fixedValues = tryToFixValues(values.get(), defaultDataContainer);
      } catch (IllegalArgumentException e) {
        return Optional.empty();
      }

      return Optional.of(new DataContainer(defaultDataContainer, fixedValues));
    }

    return Optional.empty();
  }

  public Optional<DataContainer> showDataContainerPreCompiled(final DataContainer dataContainer) {
    final Dialog<DataContainer> fillRunConfiguration = new Dialog<>();
    fillRunConfiguration.setTitle("Data");

    final Parent root;

    try {
      root = FXMLLoader.load(IdeController.class.getResource("structure/fillcontainer.fxml"));
    } catch (IOException e) {
      new Alert(Alert.AlertType.ERROR, "Unable to open run configuration dialog :(", ButtonType.OK).show();
      return Optional.empty();
    }

    final TabPane structuresTabPane = ((TabPane) root.lookup("#configurations"));
    final StructureDataContainer structure = context.getStructureFromId(dataContainer.getParentId());

    final Tab tab = new DataStructureTab(structure.getName(), structure);
    final ObservableList<PropertySheet.Item> items = FXCollections.observableArrayList();
    final ImmutableMap<String, Object> currentValues = dataContainer.getValues();

    structure.getFields().forEach((name, fieldInfo) -> {
      final SimplePropertySheetItem item = new SimplePropertySheetItem(name, fieldInfo);

      item.setValue(tryToFixValue(String.class.cast(currentValues.get(name)), String.class, fieldInfo.getType()));

      items.add(item);
    });

    final PropertySheet propertySheet = new PropertySheet(items);
    propertySheet.searchBoxVisibleProperty().set(false);
    propertySheet.setModeSwitcherVisible(false);

    propertySheet.setPropertyEditorFactory(new CustomPropertyEditorFactory());

    tab.setContent(propertySheet);

    structuresTabPane.getTabs().add(tab);

    fillRunConfiguration.getDialogPane().setContent(root);
    fillRunConfiguration.getDialogPane().getButtonTypes().add(ButtonType.APPLY);
    fillRunConfiguration.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

    fillRunConfiguration.setResultConverter(new Callback<ButtonType, DataContainer>() {
      @Override
      public DataContainer call(final ButtonType param) {
        if (param == ButtonType.CLOSE) {
          return null;
        }

        final DataStructureTab activeTab = (DataStructureTab) structuresTabPane.getSelectionModel().getSelectedItem();

        final boolean allFieldsCorrect = propertySheet
            .getItems()
            .stream()
            .anyMatch(item -> ((SimplePropertySheetItem) item).check(item.getValue()));

        if (!allFieldsCorrect) {
          return null;
        }

        final Map<String, Object> values = propertySheet
            .getItems()
            .stream()
            .collect(Collectors.toMap(PropertySheet.Item::getName, PropertySheet.Item::getValue));

        return new DataContainer(activeTab.getStructureDataContainer(), values);
      }
    });

    return fillRunConfiguration.showAndWait();
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> tryToFixValues(final Map<String, Object> values, final int defaultDataContainer) {
    final StructureDataContainer structure = context.getStructureFromId(defaultDataContainer);
    final ImmutableMap<String, StructureFieldInfo> fields = structure.getFields();
    final Converters converters = new Converters();
    return values
        .entrySet()
        .stream()
        .collect(toMap(Map.Entry::getKey, entry -> {
          final StructureFieldInfo structureFieldInfo = fields.get(entry.getKey());
          final Class<?> destinationType = structureFieldInfo.getType();
          final Object fixedValue = converters.convert(String.class, destinationType, entry.getValue().toString());

          if (!structureFieldInfo.getValidator().test(fixedValue)) {
            throw new IllegalArgumentException(fixedValue + " is not valid anymore");
          }

          return fixedValue;
        }));
  }

  @SuppressWarnings("unchecked")
  private <S, D> D tryToFixValue(final S value, final Class<S> source, final Class<D> destination) {
    final Converters converters = new Converters();
    return converters.convert(source, destination, value);
  }
}
