package com.besaba.revonline.snippetide.api.events.compile;

import com.besaba.revonline.snippetide.api.events.Event;
import com.besaba.revonline.snippetide.api.events.EventKind;
import com.besaba.revonline.snippetide.api.language.Language;

import java.nio.file.Path;

public class CompileStartEvent extends Event<Language> {
  private final Path sourceFile;
  private final Path outputDirectory;

  CompileStartEvent(final Language target, Path sourceFile, Path outputDirectory) {
    super(target);
    this.sourceFile = sourceFile;
    this.outputDirectory = outputDirectory;
  }

  public EventKind getType() {
    return EventKind.COMPILE_START;
  }

  public Path getSourceFile() {
    return sourceFile;
  }

  public Path getOutputDirectory() {
    return outputDirectory;
  }

}
