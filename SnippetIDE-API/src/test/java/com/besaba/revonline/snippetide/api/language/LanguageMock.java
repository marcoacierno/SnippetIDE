package com.besaba.revonline.snippetide.api.language;

import com.besaba.revonline.snippetide.api.events.Event;
import com.besaba.revonline.snippetide.api.events.EventKind;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

public class LanguageMock implements Language {
  @NotNull
  public String getName() {
    return "Mock";
  }

  @NotNull
  public String[] getExtensions() {
    return new String[] {"java"};
  }

  public Set<EventKind> listenTo() {
    return EnumSet.of(EventKind.COMPILE_START);
  }

  public boolean receiveEvent(Event event) {
    return false;
  }
}
