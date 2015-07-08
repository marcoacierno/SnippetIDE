package com.besaba.revonline.snippetide.api.events.compile;

import com.besaba.revonline.snippetide.api.language.Language;

import java.nio.file.Path;

public class CompileStartEventBuilder {
  private Language target;
  private Path sourceFile;
  private Path outputDirectory;

  public CompileStartEventBuilder setTarget(final Language target) {
    this.target = target;
    return this;
  }

  public CompileStartEventBuilder setSourceFile(Path sourceFile) {
    this.sourceFile = sourceFile;
    return this;
  }

  public CompileStartEventBuilder setOutputDirectory(Path outputDirectory) {
    this.outputDirectory = outputDirectory;
    return this;
  }

  public CompileStartEvent build() {
    return new CompileStartEvent(
        target,
        sourceFile,
        outputDirectory
    );
  }
}
