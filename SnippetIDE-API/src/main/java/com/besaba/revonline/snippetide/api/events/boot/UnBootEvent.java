package com.besaba.revonline.snippetide.api.events.boot;

import com.besaba.revonline.snippetide.api.events.Event;

/**
 * Listen to this if you have any resource
 * you want to free when the application
 * ends.
 *
 * Or to force kill something, save data
 * etc.
 *
 * Don't save the configuration or anything.
 * Your job in this event is to cleanup
 * YOUR RESOURCES not other things.
 */
public class UnBootEvent extends Event<Void> {
  public UnBootEvent() {
    super(null);
  }
}
