package com.besaba.revonline.snippetide.api.plugins;

import java.nio.file.Path;

public class UnableToLoadPluginException extends RuntimeException {
  private final Path fileLocation;
  private final PluginManager pluginManager;

  public UnableToLoadPluginException(final Path fileLocation, final PluginManager pluginManager) {
    this.fileLocation = fileLocation;
    this.pluginManager = pluginManager;
  }

  public UnableToLoadPluginException(final String message, final Path fileLocation, final PluginManager pluginManager) {
    super(message);
    this.fileLocation = fileLocation;
    this.pluginManager = pluginManager;
  }

  public UnableToLoadPluginException(final String message, final Throwable cause, final Path fileLocation, final PluginManager pluginManager) {
    super(message, cause);
    this.fileLocation = fileLocation;
    this.pluginManager = pluginManager;
  }

  public UnableToLoadPluginException(final Throwable cause, final Path fileLocation, final PluginManager pluginManager) {
    super(cause);
    this.fileLocation = fileLocation;
    this.pluginManager = pluginManager;
  }

  public UnableToLoadPluginException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace, final Path fileLocation, final PluginManager pluginManager) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.fileLocation = fileLocation;
    this.pluginManager = pluginManager;
  }

  public PluginManager getPluginManager() {
    return pluginManager;
  }

  public Path getFileLocation() {
    return fileLocation;
  }
}
