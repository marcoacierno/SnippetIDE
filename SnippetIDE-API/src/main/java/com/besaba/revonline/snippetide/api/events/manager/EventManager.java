package com.besaba.revonline.snippetide.api.events.manager;

import com.besaba.revonline.snippetide.api.events.Event;
import org.jetbrains.annotations.NotNull;

/**
 * The EventManager handles the communicate between
 * the components of the application.
 *
 * The implementation can both use an EventBus (example, Guava EventBus)
 * or a custom implementation with Maps.
 *
 * A
 */
public interface EventManager {
  /**
   * The listener passed to this method should be
   * registered inside the manager and should receive
   * the events
   *
   * @param listener The listener
   */
  void registerListener(@NotNull final Object listener);

  /**
   * Should remove the listener from the listeners
   *
   * @param listener The listener to remove
   */
  void unregisterListener(@NotNull final Object listener);

  /**
   * Used by the other components to propagate an event
   * to the listeners.
   *
   * @param event The event to propagate
   */
  void post(@NotNull final Event<?> event);
}
