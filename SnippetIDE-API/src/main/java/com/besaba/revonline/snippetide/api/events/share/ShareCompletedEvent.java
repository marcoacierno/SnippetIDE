package com.besaba.revonline.snippetide.api.events.share;

import com.besaba.revonline.snippetide.api.events.Event;
import org.jetbrains.annotations.NotNull;

public class ShareCompletedEvent extends Event<Void> {
  @NotNull
  private final String data;

  public ShareCompletedEvent(@NotNull final String data) {
    super(null);
    this.data = data;
  }

  @NotNull
  public String getData() {
    return data;
  }
}
