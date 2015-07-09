package com.besaba.revonline.snippetide.api.events.manager.impl;

import com.besaba.revonline.snippetide.api.events.Event;
import com.besaba.revonline.snippetide.api.events.manager.EventManager;
import com.google.common.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

public class EventBusEventManager implements EventManager {
  private final EventBus eventBus = new EventBus();

  public void registerListener(@NotNull final Object listener) {
    eventBus.register(listener);
  }

  public void unregisterListener(@NotNull final Object listener) {
    eventBus.unregister(listener);
  }

  public void post(@NotNull final Event<?> event) {
    eventBus.post(event);
  }
}
