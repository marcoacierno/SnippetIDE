package test;

import com.besaba.revonline.snippetide.api.language.Language;
import org.jetbrains.annotations.NotNull;

public class TestLanguage implements Language {
  @NotNull
  public String getName() {
    return "TestLanguage";
  }

  @NotNull
  public String[] getExtensions() {
    return new String[] {"test", "language"};
  }
}
