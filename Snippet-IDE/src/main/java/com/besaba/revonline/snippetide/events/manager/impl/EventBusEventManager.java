package com.besaba.revonline.snippetide.events.manager.impl;

import com.besaba.revonline.snippetide.api.events.Event;
import com.besaba.revonline.snippetide.api.events.manager.EventManager;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class EventBusEventManager implements EventManager {
  private final static Logger logger = Logger.getLogger(EventBusEventManager.class);

  private final EventBus eventBus = new EventBus((throwable, subscriberExceptionContext) -> {
    logger.fatal("Exception inside the EventBus");
    logger.fatal("Throwable", throwable);
    logger.fatal("Event: " + subscriberExceptionContext.getEvent());
    logger.fatal("Method: " + subscriberExceptionContext.getSubscriberMethod());
  });

  {
    eventBus.register(this);
  }

  public void registerListener(@NotNull final Object listener) {
    eventBus.register(listener);
  }

  public void unregisterListener(@NotNull final Object listener) {
    eventBus.unregister(listener);
  }

  public void post(@NotNull final Event<?> event) {
    eventBus.post(event);
  }

  @Subscribe
  public void deadEvent(DeadEvent deadEvent) {
    logger.fatal("Dead event!");
    logger.fatal("Event: " + deadEvent.getEvent());
    logger.fatal("Source: " + deadEvent.getSource());
  }
}
