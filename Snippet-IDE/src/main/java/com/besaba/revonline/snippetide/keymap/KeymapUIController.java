package com.besaba.revonline.snippetide.keymap;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination.ModifierValue;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.Optional;

public class KeymapUIController {
  @FXML
  private TableView<Action> keymapTable;
  @FXML
  private TableColumn<Action, String> combination;
  @FXML
  private TableColumn<Action, String> keyAction;

  public void initialize() {
    keyAction.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().toString()));
    combination.setCellValueFactory(param -> {
      final KeyCodeCombination combination = Keymap.getCombination(param.getValue());
      return new ReadOnlyObjectWrapper<>(combination == null ? "" : combination.getDisplayText());
    });

    keymapTable.setOnMouseClicked(this::onItemClick);
    keymapTable.getItems().addAll(Action.values());
  }

  private void onItemClick(final MouseEvent mouseEvent) {
    if (mouseEvent.getClickCount() < 2) {
      return;
    }

    final Node root;

    try {
      root = FXMLLoader.load(KeymapUIController.class.getResource("associatekey.fxml"));
    } catch (IOException e) {
      new Alert(Alert.AlertType.ERROR, "Unable to open associate-key view. Try again", ButtonType.OK).showAndWait();
      return;
    }

    final TextField actionTextField = (TextField) root.lookup("#actionTextArea");
    final KeyCodeCombination[] combination = new KeyCodeCombination[1];

    actionTextField.setOnKeyTyped(Event::consume);
    actionTextField.setOnKeyPressed(event -> {
      event.consume();

      final KeyCode keyCode = event.getCode();

      final ModifierValue shift     = event.isShiftDown()     ? ModifierValue.DOWN : ModifierValue.UP;
      final ModifierValue control   = event.isControlDown()   ? ModifierValue.DOWN : ModifierValue.UP;
      final ModifierValue alt       = event.isAltDown()       ? ModifierValue.DOWN : ModifierValue.UP;
      final ModifierValue meta      = event.isMetaDown()      ? ModifierValue.DOWN : ModifierValue.UP;
      final ModifierValue shortcut  = event.isShortcutDown()  ? ModifierValue.DOWN : ModifierValue.UP;

      combination[0] = new KeyCodeCombination(keyCode, shift, control, alt, meta, shortcut);
      actionTextField.setText(combination[0].getDisplayText());
    });

    final Dialog<KeyCodeCombination> alert = new Dialog<>();
    alert.getDialogPane().getButtonTypes().add(new ButtonType("Apply", ButtonBar.ButtonData.APPLY));
    alert.getDialogPane().getButtonTypes().add(new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE));
    alert.getDialogPane().setContent(root);

    alert.setResultConverter(param -> {
      if (param.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
        return null;
      }

      return combination[0];
    });

    actionTextField.requestFocus();
    final Optional<KeyCodeCombination> combinationOptional = alert.showAndWait();

    combinationOptional.ifPresent(newCombination -> {
      final Action selectedAction = keymapTable.getSelectionModel().getSelectedItem();
      Keymap.updateCombination(selectedAction, newCombination);

      // update key in the tableview
      keymapTable.getColumns().get(0).setVisible(false);
      keymapTable.getColumns().get(0).setVisible(true);
    });
  }
}
