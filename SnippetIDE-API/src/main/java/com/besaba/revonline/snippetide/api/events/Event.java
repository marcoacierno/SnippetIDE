package com.besaba.revonline.snippetide.api.events;

public abstract class Event {
  private boolean handled;

  public boolean getHandled() {
    return handled;
  }

  public void setHandled(boolean handled) {
    this.handled = handled;
  }

  public abstract EventKind getType();
}
