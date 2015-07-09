package com.besaba.revonline.snippetide.api.compiler;

public class CompilationProblemBuilder {
  private long line;
  private String message;
  private CompilationProblemType type;

  public CompilationProblemBuilder setLine(final long line) {
    this.line = line;
    return this;
  }

  public CompilationProblemBuilder setMessage(final String message) {
    this.message = message;
    return this;
  }

  public CompilationProblemBuilder setType(final CompilationProblemType type) {
    this.type = type;
    return this;
  }

  public CompilationProblem createCompilationProblem() {
    return new CompilationProblem(line, message, type);
  }
}