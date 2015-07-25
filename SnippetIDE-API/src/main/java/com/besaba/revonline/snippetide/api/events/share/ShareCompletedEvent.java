package com.besaba.revonline.snippetide.api.events.share;

import com.besaba.revonline.snippetide.api.events.Event;
import com.besaba.revonline.snippetide.api.shareservices.ShareService;
import org.jetbrains.annotations.NotNull;

public class ShareCompletedEvent extends Event<Void> {
  @NotNull
  private final String data;
  @NotNull
  private final ShareService service;

  public ShareCompletedEvent(@NotNull final String data, @NotNull final ShareService service) {
    super(null);
    this.data = data;
    this.service = service;
  }

  @NotNull
  public String getData() {
    return data;
  }

  @NotNull
  public ShareService getService() {
    return service;
  }
}
