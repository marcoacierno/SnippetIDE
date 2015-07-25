package com.besaba.revonline.snippetide.api.events.share;

import com.besaba.revonline.snippetide.api.events.Event;
import com.besaba.revonline.snippetide.api.shareservices.ShareService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShareFailedEvent extends Event<Void> {
  @Nullable
  private final Throwable throwable;
  @NotNull
  private final String reason;
  @NotNull
  private final ShareService service;

  public ShareFailedEvent(final @Nullable Throwable throwable, final @NotNull String reason, final @NotNull ShareService service) {
    super(null);
    this.throwable = throwable;
    this.reason = reason;
    this.service = service;
  }

  @Nullable
  public Throwable getThrowable() {
    return throwable;
  }

  @NotNull
  public String getReason() {
    return reason;
  }

  @NotNull
  public ShareService getService() {
    return service;
  }
}
