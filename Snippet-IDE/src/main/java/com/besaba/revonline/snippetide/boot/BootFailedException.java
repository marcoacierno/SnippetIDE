package com.besaba.revonline.snippetide.boot;

public class BootFailedException extends RuntimeException {
  public BootFailedException() {
  }

  public BootFailedException(final String message) {
    super(message);
  }

  public BootFailedException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public BootFailedException(final Throwable cause) {
    super(cause);
  }

  public BootFailedException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
