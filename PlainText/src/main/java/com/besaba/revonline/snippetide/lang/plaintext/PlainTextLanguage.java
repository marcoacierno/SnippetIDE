package com.besaba.revonline.snippetide.lang.plaintext;

import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.run.RunConfiguration;
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

  @NotNull
  @Override
  public String getTemplate() {
    return "Text";
  }

  @NotNull
  @Override
  public RunConfiguration[] getRunConfigurations() {
    return new RunConfiguration[0];
  }
}
