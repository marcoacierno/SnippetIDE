package com.besaba.revonline.snippetide.api.events.run;

import com.besaba.revonline.snippetide.api.events.Event;

public class SendMessageToProcessEvent extends Event<Void> {
  private final String message;

  public SendMessageToProcessEvent(final String message) {
    super(null);
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
