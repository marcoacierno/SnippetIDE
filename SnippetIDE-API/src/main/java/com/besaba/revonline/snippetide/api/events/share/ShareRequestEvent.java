package com.besaba.revonline.snippetide.api.events.share;

import com.besaba.revonline.snippetide.api.events.Event;
import com.besaba.revonline.snippetide.api.shareservices.ShareService;
import org.jetbrains.annotations.NotNull;

public class ShareRequestEvent extends Event<ShareService> {
  @NotNull
  private final String fileName;
  @NotNull
  private final String code;

  public ShareRequestEvent(@NotNull final ShareService target,
                           @NotNull final String fileName,
                           @NotNull final String code) {
    super(target);
    this.fileName = fileName;
    this.code = code;
  }

  @NotNull
  public String getFileName() {
    return fileName;
  }

  @NotNull
  public String getCode() {
    return code;
  }
}
