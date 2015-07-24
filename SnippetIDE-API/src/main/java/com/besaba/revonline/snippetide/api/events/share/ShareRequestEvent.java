package com.besaba.revonline.snippetide.api.events.share;

import com.besaba.revonline.snippetide.api.events.Event;
import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.shareservices.ShareService;
import org.jetbrains.annotations.NotNull;

public class ShareRequestEvent extends Event<ShareService> {
  @NotNull
  private final String fileName;
  @NotNull
  private final String code;
  @NotNull
  private final Language language;

  public ShareRequestEvent(@NotNull final ShareService target,
                           @NotNull final String fileName,
                           @NotNull final String code,
                           @NotNull final Language language) {
    super(target, true);
    this.fileName = fileName;
    this.code = code;
    this.language = language;
  }

  @NotNull
  public String getFileName() {
    return fileName;
  }

  @NotNull
  public String getCode() {
    return code;
  }

  @NotNull
  public Language getLanguage() {
    return language;
  }
}
