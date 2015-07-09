package com.besaba.revonline.snippetide.api.events;

public abstract class Event<T> {
  private T target;

  protected Event(T target) {
    this.target = target;
  }

  public T getTarget() {
    return target;
  }
}
