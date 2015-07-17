package com.besaba.revonline.snippetide.api.configuration;

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
