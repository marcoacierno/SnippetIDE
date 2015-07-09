package com.besaba.revonline.snippetide.api.compiler;

import org.jetbrains.annotations.NotNull;

public final class CompilationProblem {
  @NotNull
  private final CompilationProblemType type;
  @NotNull
  private final String message;
  private final long line;

  CompilationProblem(final long line, @NotNull final String message, @NotNull final CompilationProblemType type) {
    this.line = line;
    this.message = message;
    this.type = type;
  }

  @NotNull
  public CompilationProblemType getType() {
    return type;
  }

  @NotNull
  public String getMessage() {
    return message;
  }

  public long getLine() {
    return line;
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof CompilationProblem)) {
      return false;
    }

    final CompilationProblem other = ((CompilationProblem) obj);
    return
        other.line == line &&
        other.message.equals(message) &&
        other.type == type;
  }

  @Override
  public int hashCode() {
    // yes it can be generated, but who cares
    int result = 17;

    result = 31 * (int) (line ^ (line >>> 32)) + result;
    result = 31 * message.hashCode() + result;
    result = 31 * type.hashCode() + result;

    return result;
  }
}
