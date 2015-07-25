package com.besaba.revonline.snippetide.api.events;

public abstract class Event<T> {
  private T target;
  private final boolean useNewThread;

  protected Event(T target, final boolean useNewThread) {
    this.target = target;
    this.useNewThread = useNewThread;
  }

  protected Event(T target) {
    this.target = target;
    this.useNewThread = false;
  }

  public T getTarget() {
    return target;
  }

  public boolean isUseNewThread() {
    return useNewThread;
  }
}
