package com.besaba.revonline.snippetide.api.events.run;

import com.besaba.revonline.snippetide.api.events.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A Plugin should send this event when
 * the IDE asks them to run a snippet.
 */
public class RunInformationEvent extends Event<Void> {
  @Nullable
  private final String command;
  @Nullable
  private final RunStartEvent runStartEvent;
  private final boolean externalProcess;

  public RunInformationEvent(   @Nullable final String command,
                                @Nullable final RunStartEvent runStartEvent) {
    this(command, runStartEvent, true);
  }

  public RunInformationEvent(@Nullable final String command, @Nullable final RunStartEvent runStartEvent, final boolean externalProcess) {
    super(null);
    this.command = command;
    this.runStartEvent = runStartEvent;
    this.externalProcess = externalProcess;
  }

  public static RunInformationEvent noExternalProcess() {
    return new RunInformationEvent(null, null, false);
  }

  @Nullable
  public String getCommand() {
    return command;
  }

  @Nullable
  public RunStartEvent getRunStartEvent() {
    return runStartEvent;
  }

  public boolean needExternalProcess() {
    return externalProcess;
  }
}
