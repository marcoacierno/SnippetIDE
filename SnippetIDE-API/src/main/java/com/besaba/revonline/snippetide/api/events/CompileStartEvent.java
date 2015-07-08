package com.besaba.revonline.snippetide.api.events;

public class CompileStartEvent extends Event {
  public EventKind getType() {
    return EventKind.COMPILE_START;
  }
}
