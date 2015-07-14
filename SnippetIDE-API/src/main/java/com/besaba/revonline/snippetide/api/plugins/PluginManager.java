package com.besaba.revonline.snippetide.api.plugins;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * A plugin manager is a manager that should
 * be able to load the plugins of the ide.
 *
 * A Plugin is always a file (which can be a Jar file
 * as well as a zip file).
 */
public interface PluginManager {
  /**
   * Called when the IDE asks to load a plugin.
   * This method should load all the information about
   * the plugin and return the object.
   *
   * The manager should also save the nearly created internally.
   *
   * There cannot be two plugins with the same name,
   * every Manager can decide how to handle this situation
   * (discard the first and save this, keep only the first found etc.)
   *
   * @param file The file where the plugin is
   * @return The plugin created
   * @throws UnableToLoadPluginException throw when the file passed
   *                                     is not a plugin or is invalid
   */
  @NotNull
  Plugin loadPlugin(@NotNull final Path file);

  /**
   * Search a plugin by name (There cannot be two plugins with the same name)
   *
   * @param pluginName The name of the plugin
   * @return The plugin loaded or Nothing
   */
  @NotNull
  Optional<Plugin> searchPluginByName(@NotNull final String pluginName);

  /**
   * @return How many plugins it has loaded
   */
  long getPluginsCount();

  /**
   * It should return a list of all plugins loaded
   * by the PluginManager.
   *
   * If there are no plugins, an empty list should be returned
   * not null.
   *
   * @return An immutable list of all the plugins loaded by the PluginManager
   */
  @NotNull
  List<Plugin> getPlugins();
}
