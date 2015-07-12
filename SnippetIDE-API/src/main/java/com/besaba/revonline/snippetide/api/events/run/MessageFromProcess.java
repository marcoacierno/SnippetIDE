package com.besaba.revonline.snippetide.api.events.run;

import com.besaba.revonline.snippetide.api.events.Event;
import org.jetbrains.annotations.NotNull;

public class MessageFromProcess extends Event<Void> {
  @NotNull
  private final String message;

  public MessageFromProcess(final @NotNull String message) {
    super(null);
    this.message = message;
  }

  @NotNull
  public String getMessage() {
    return message;
  }
}
