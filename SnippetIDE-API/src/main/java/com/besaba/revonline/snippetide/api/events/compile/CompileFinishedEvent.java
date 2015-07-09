package com.besaba.revonline.snippetide.api.events.compile;

import com.besaba.revonline.snippetide.api.compiler.CompilationResult;
import com.besaba.revonline.snippetide.api.events.Event;
import com.besaba.revonline.snippetide.api.language.Language;

import java.util.List;

public class CompileFinishedEvent extends Event<Language> {
  private final CompilationResult compilationResult;

  public CompileFinishedEvent(final Language target, final CompilationResult compilationResult) {
    super(target);
    this.compilationResult = compilationResult;
  }

  public CompilationResult getCompilationResult() {
    return compilationResult;
  }
}
