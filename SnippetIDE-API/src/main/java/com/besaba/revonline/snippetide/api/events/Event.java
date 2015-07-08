package com.besaba.revonline.snippetide.api.events;

public abstract class Event<T> {
  private boolean handled;
  private T target;

  protected Event(T target) {
    this.target = target;
  }

  public T getTarget() {
    return target;
  }

  public boolean getHandled() {
    return handled;
  }

  public void setHandled(boolean handled) {
    this.handled = handled;
  }

  public abstract EventKind getType();
}
