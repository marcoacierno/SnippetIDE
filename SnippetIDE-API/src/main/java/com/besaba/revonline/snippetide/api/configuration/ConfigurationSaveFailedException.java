package com.besaba.revonline.snippetide.api.configuration;

/**
 * Called when the configuration failed to write
 * the settings to the OutputStream passed.
 */
public class ConfigurationSaveFailedException extends Exception {
  public ConfigurationSaveFailedException() {
  }

  public ConfigurationSaveFailedException(final String message) {
    super(message);
  }

  public ConfigurationSaveFailedException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public ConfigurationSaveFailedException(final Throwable cause) {
    super(cause);
  }

  public ConfigurationSaveFailedException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
