package com.besaba.revonline.snippetide;

import com.besaba.revonline.snippetide.api.application.IDEApplication;
import com.besaba.revonline.snippetide.api.application.IDEApplicationLauncher;
import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.plugins.Plugin;
import com.besaba.revonline.snippetide.api.plugins.PluginManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PluginsListController {
  private final static Logger logger = Logger.getLogger(PluginsListController.class);

  @FXML
  private ListView<Plugin> pluginsList;
  @FXML
  private WebView pluginInfo;
  private WebEngine webEngine;
  @NotNull
  private final IDEApplication application = IDEApplicationLauncher.getIDEApplication();
  @NotNull
  private final PluginManager pluginManager = application.getPluginManager();

  public void initialize() {
    preparePluginsListView();
  }

  private void preparePluginsListView() {
    webEngine = pluginInfo.getEngine();

    pluginsList.setCellFactory(param -> new ListCell<Plugin>() {
      @Override
      protected void updateItem(final Plugin item, final boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
          setText(null);
        } else {
          setText(item.getName());
        }

        setGraphic(null);
      }
    });

    pluginsList.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
      final Plugin selectedPlugin = newValue;
      logger.debug("choose plugin -> " + selectedPlugin);
      showInformationAboutPlugin(selectedPlugin);
    }));

    pluginsList.getItems().addAll(pluginManager.getPlugins());
  }

  private void showInformationAboutPlugin(final Plugin plugin) {
    logger.debug("showInformationAboutPlugin called");
    logger.debug("Plugin: " + plugin);

    webEngine.setJavaScriptEnabled(true);
    webEngine.load(PluginsListController.class.getResource("plugindescription.html").toExternalForm());
    webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
      logger.debug("load worker newValue = " + newValue);
      if (newValue != Worker.State.SUCCEEDED) {
        return;
      }

      final String script = String.format("var plugin = {\n" +
              "  name: \"%s\",\n" +
              "  description: \"%s\",\n" +
              "  version: \"%s\",\n" +
              "  minIdeVersion: \"%s\",\n" +
              "  authors: [%s],\n" +
              "  languages: [%s]\n" +
              "};" +
              "" +
              "injectData();",
          plugin.getName(),
          plugin.getDescription(),
          plugin.getVersion().toString(),
          plugin.getMinIdeVersion().toString(),
          Arrays.stream(plugin.getAuthors()).map(author -> "\"" + author + "\"").reduce("", (acc, nxt) -> nxt + "," + acc),
          plugin.getLanguages().stream().map(Language::getName).reduce("", (acc, nxt) -> "\"" + nxt + "\"," + acc)
      );

      logger.debug("script to inject");
      logger.debug(script);

      webEngine.executeScript(script);
    });
  }
}
