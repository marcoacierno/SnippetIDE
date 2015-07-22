package com.besaba.revonline.snippetide.api.configuration;

/**
 * Thrown when something went wrong during the loading
 * of a configuration.
 *
 * Implementation can throw by exception if the InputStream
 * passed is invalid, doesn't contains correct settings or
 * if something else went wrong.
 *
 * Try to catch any exception and wrap them in this one.
 *
 * It's a checked exception because the programmer <b>should</b>
 * try to recover from this exception by loading an alternative
 * configuration or at least inform the user about the reason
 * of the error.
 *
 */
public class ConfigurationLoadFailedException extends Exception {
  public ConfigurationLoadFailedException() {
  }

  public ConfigurationLoadFailedException(final String message) {
    super(message);
  }

  public ConfigurationLoadFailedException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public ConfigurationLoadFailedException(final Throwable cause) {
    super(cause);
  }

  public ConfigurationLoadFailedException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
