package com.besaba.revonline.snippetide;

import com.besaba.revonline.snippetide.api.events.compile.CompileStartEvent;
import com.besaba.revonline.snippetide.api.language.Language;
import com.google.common.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

public class MockLanguage implements Language {
  public static final MockLanguage INSTANCE = new MockLanguage();

  public boolean compileCalled = false;
  public CompileStartEvent compileStartEvent = null;

  private MockLanguage() {}

  @NotNull
  @Override
  public String getName() {
    return "Mock";
  }

  @NotNull
  @Override
  public String[] getExtensions() {
    return new String[] {"pwn"};
  }

  @Subscribe
  public void compile(final CompileStartEvent compileStartEvent) {
    compileCalled = true;
    this.compileStartEvent = compileStartEvent;
  }
}
