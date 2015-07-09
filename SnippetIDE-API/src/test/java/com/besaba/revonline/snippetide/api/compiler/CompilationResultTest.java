package com.besaba.revonline.snippetide.api.compiler;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class CompilationResultTest {
  private CompilationResult failedCompilationWithOneErrorAndZeroWarnings;
  private CompilationResult successfulCompilationWithZeroErrorsAndZeroWarnings;
  private CompilationResult successfulCompilationWithZeroErrorsAndThreeWarnings;
  private CompilationResult failedCompilationWithTwoErrorsAndOneWarning;

  @Before
  public void setUp() throws Exception {
    prepareCompilationWithOneErrorAndZeroWarnings();
    prepareCompilationWithZeroErrorsAndZeroWarnings();
    prepareCompilationWithZeroErrorsAndThreeWarnings();
    prepareCompilationWithTwoErrorsAndOneWarning();
  }

  // <editor-fold desc="Preparing test variables">

  private void prepareCompilationWithOneErrorAndZeroWarnings() {
    final List<CompilationProblem> problems = Arrays.asList(
        new CompilationProblemBuilder()
            .setLine(1)
            .setMessage("Fake error")
            .setType(CompilationProblemType.Error)
            .createCompilationProblem()
    );

    failedCompilationWithOneErrorAndZeroWarnings = new CompilationResult(problems);
  }

  private void prepareCompilationWithZeroErrorsAndZeroWarnings() {
    final List<CompilationProblem> problems = Collections.emptyList();
    successfulCompilationWithZeroErrorsAndZeroWarnings = new CompilationResult(problems);
  }

  private void prepareCompilationWithZeroErrorsAndThreeWarnings() {
    final List<CompilationProblem> problems = Arrays.asList(
        new CompilationProblemBuilder()
            .setLine(253)
            .setMessage("Warning #1")
            .setType(CompilationProblemType.Warning)
            .createCompilationProblem(),
        new CompilationProblemBuilder()
            .setLine(870)
            .setMessage("Warning #2")
            .setType(CompilationProblemType.Warning)
            .createCompilationProblem(),
        new CompilationProblemBuilder()
            .setLine(13)
            .setMessage("Warning #3")
            .setType(CompilationProblemType.Warning)
            .createCompilationProblem()
    );

    successfulCompilationWithZeroErrorsAndThreeWarnings = new CompilationResult(problems);
  }

  private void prepareCompilationWithTwoErrorsAndOneWarning() {
    final List<CompilationProblem> problems = Arrays.asList(
        new CompilationProblemBuilder()
          .setLine(100)
          .setMessage("Error #1")
          .setType(CompilationProblemType.Error)
          .createCompilationProblem(),
        new CompilationProblemBuilder()
          .setLine(123)
          .setMessage("Error #2")
          .setType(CompilationProblemType.Error)
          .createCompilationProblem(),
        new CompilationProblemBuilder()
          .setLine(500)
          .setMessage("Warning #1")
          .setType(CompilationProblemType.Warning)
          .createCompilationProblem()
    );
    failedCompilationWithTwoErrorsAndOneWarning = new CompilationResult(problems);
  }

  // </editor-fold>

  @Test
  public void testSuccessfulCompilationButNotCleanBecauseItHasAWarning() throws Exception {
    assertTrue(successfulCompilationWithZeroErrorsAndThreeWarnings.successfulCompilation());
    assertFalse(successfulCompilationWithZeroErrorsAndThreeWarnings.cleanCompilation());
    assertTrue(successfulCompilationWithZeroErrorsAndThreeWarnings.successfulCompilation());
  }

  @Test
  public void testCleanCompilationWithAFailedCompilationWithTwoErrorsAndOneWarning() throws Exception {
    assertFalse(failedCompilationWithTwoErrorsAndOneWarning.cleanCompilation());
    assertFalse(failedCompilationWithTwoErrorsAndOneWarning.successfulCompilation());
    assertTrue(failedCompilationWithTwoErrorsAndOneWarning.failedCompilation());
  }

  @Test
  public void testGetProblems() throws Exception {
    assertThat(failedCompilationWithOneErrorAndZeroWarnings.getProblems(), hasItem(
      new CompilationProblemBuilder()
        .setLine(1)
        .setMessage("Fake error")
        .setType(CompilationProblemType.Error)
        .createCompilationProblem()
    ));
  }

  @Test
  public void testGetErrors() throws Exception {
    assertThat(failedCompilationWithOneErrorAndZeroWarnings.getErrors().size(), is(1));

    assertThat(failedCompilationWithOneErrorAndZeroWarnings.getErrors(), hasItem(
        new CompilationProblemBuilder()
            .setLine(1)
            .setMessage("Fake error")
            .setType(CompilationProblemType.Error)
            .createCompilationProblem()
    ));
  }

  @Test
  public void testGetWarnings() throws Exception {
    assertThat(failedCompilationWithOneErrorAndZeroWarnings.getWarnings().size(), is(0));
  }

  @Test
  public void testCleanCompilation() throws Exception {
    assertFalse(failedCompilationWithOneErrorAndZeroWarnings.cleanCompilation());
  }

  @Test
  public void testFailedCompilation() throws Exception {
    assertTrue(failedCompilationWithOneErrorAndZeroWarnings.failedCompilation());
  }

  @Test
  public void testSuccessfulCompilation() throws Exception {
    assertFalse(failedCompilationWithOneErrorAndZeroWarnings.successfulCompilation());
  }
}