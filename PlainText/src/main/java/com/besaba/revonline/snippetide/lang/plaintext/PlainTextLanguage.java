package com.besaba.revonline.snippetide.lang.plaintext;

import com.besaba.revonline.snippetide.api.language.Language;
import org.jetbrains.annotations.NotNull;

public class PlainTextLanguage implements Language {
  @NotNull
  public String getName() {
    return "Plain Text";
  }

  @NotNull
  public String[] getExtensions() {
    return new String[] {"txt"};
  }
}
