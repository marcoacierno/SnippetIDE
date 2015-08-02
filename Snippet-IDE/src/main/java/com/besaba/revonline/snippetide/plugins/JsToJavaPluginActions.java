package com.besaba.revonline.snippetide.plugins;

import com.besaba.revonline.snippetide.api.application.IDEApplicationLauncher;
import com.besaba.revonline.snippetide.api.plugins.PluginManager;
import javafx.scene.control.Alert;

public class JsToJavaPluginActions {
  public void enablePlugin(final String pluginName) {
    final PluginManager pluginManager = IDEApplicationLauncher.getIDEApplication().getPluginManager();
    final boolean result = pluginManager.enablePlugin(pluginName);

    if (!result) {
      new Alert(Alert.AlertType.ERROR, "Unable to enable the plugin, maybe already enabled?").show();
    } else {
      new Alert(Alert.AlertType.INFORMATION, "Plugin enabled! Please restart the IDE").show();
    }
  }

  public void disablePlugin(final String pluginName) {
    final PluginManager pluginManager = IDEApplicationLauncher.getIDEApplication().getPluginManager();
    final boolean result = pluginManager.disablePlugin(pluginName);

    if (!result) {
      new Alert(Alert.AlertType.ERROR, "Unable to disable the plugin, maybe already disabled?").show();
    } else {
      new Alert(Alert.AlertType.INFORMATION, "Plugin disabled! Please restart the IDE").show();
    }
  }
}
