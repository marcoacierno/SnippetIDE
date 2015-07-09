package com.besaba.revonline.snippetide.api.application;

public class IDEApplicationLauncher {
  private static IDEApplication ideApplication;

  public synchronized static IDEApplication getIDEApplication() {
    if (ideApplication == null) {
      throw new IllegalStateException("ideApplication cannot be used now.");
    }

    return ideApplication;
  }

  public synchronized static IDEApplication createApplication(final IDEApplication application) {
    if (ideApplication != null) {
      throw new IllegalStateException("Use getIDEApplication to get the instance");
    }

    return ideApplication = application;
  }
}
