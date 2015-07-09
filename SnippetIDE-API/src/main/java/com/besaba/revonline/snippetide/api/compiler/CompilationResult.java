package com.besaba.revonline.snippetide.api.compiler;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class CompilationResult {
  private final ImmutableList<CompilationProblem> problems;
  private final ImmutableList<CompilationProblem> errors;
  private final ImmutableList<CompilationProblem> warnings;

  public CompilationResult(final List<CompilationProblem> problems) {
    this.problems = ImmutableList.copyOf(problems);

    final ImmutableList.Builder<CompilationProblem> errorsBuilder = new ImmutableList.Builder<>();
    final ImmutableList.Builder<CompilationProblem> warningsBuilder = new ImmutableList.Builder<>();

    problems.forEach(problem -> {
      if (problem.getType() == CompilationProblemType.Error) {
        errorsBuilder.add(problem);
      } else if (problem.getType() == CompilationProblemType.Warning) {
        warningsBuilder.add(problem);
      }
    });

    this.errors = errorsBuilder.build();
    this.warnings = warningsBuilder.build();
  }

  public ImmutableList<CompilationProblem> getProblems() {
    return problems;
  }

  public ImmutableList<CompilationProblem> getErrors() {
    return errors;
  }

  public ImmutableList<CompilationProblem> getWarnings() {
    return warnings;
  }

  public boolean cleanCompilation() {
    return successfulCompilation() && warnings.size() == 0;
  }

  public boolean failedCompilation() {
    return !successfulCompilation();
  }

  public boolean successfulCompilation() {
    return errors.size() == 0;
  }
}
