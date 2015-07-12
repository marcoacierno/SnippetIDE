package com.besaba.revonline.snippetide.api.events.run;

import com.besaba.revonline.snippetide.api.events.Event;
import org.jetbrains.annotations.NotNull;

/**
 * A Plugin should send this event when
 * the IDE asks them to run a snippet.
 */
public class RunInformationEvent extends Event<Void> {
  @NotNull
  private final String command;
  @NotNull
  private final RunStartEvent runStartEvent;

  public RunInformationEvent(   @NotNull final String command,
                                @NotNull final RunStartEvent runStartEvent) {
    super(null);
    this.command = command;
    this.runStartEvent = runStartEvent;
  }

  @NotNull
  public String getCommand() {
    return command;
  }

  @NotNull
  public RunStartEvent getRunStartEvent() {
    return runStartEvent;
  }
}
