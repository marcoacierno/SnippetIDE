package com.besaba.revonline.snippetide.api.compiler;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class CompilationProblemTest {
  private CompilationProblem errorAtLine10WithMessageHelloWorld;

  @Before
  public void setUp() throws Exception {
    errorAtLine10WithMessageHelloWorld = new CompilationProblem(10, "Hello world", CompilationProblemType.Error);
  }

  @Test
  public void testTwoNotEqualsProblemsWithSameErrorTypeAndMessageMessageButDifferentLine() throws Exception {
    final CompilationProblem errorsWithMessageHelloWorldAtLine22 = new CompilationProblem(22, "Hello world", CompilationProblemType.Error);
    assertNotEquals(errorsWithMessageHelloWorldAtLine22, errorAtLine10WithMessageHelloWorld);
  }

  @Test
  public void testTwoEqualsErrors() throws Exception {
    final CompilationProblem localErrorAtLine10WithMessageHelloWorld = new CompilationProblem(10, "Hello world", CompilationProblemType.Error);
    assertEquals(localErrorAtLine10WithMessageHelloWorld, this.errorAtLine10WithMessageHelloWorld);
  }

  @Test
  public void testOneErrorAndOneWarningAtTheSameLineAndWithTheSameMessage() throws Exception {
    final CompilationProblem warningAtLine10WithMessageHelloWorld = new CompilationProblem(10, "Hello world", CompilationProblemType.Warning);
    assertNotEquals(warningAtLine10WithMessageHelloWorld, errorAtLine10WithMessageHelloWorld);
  }

  @Test
  public void testOneErrorAndOneWarningWithDifferentMessageAndLine() throws Exception {
    final CompilationProblem warningAtLine1500WithMessageUnexceptedEndOfFile = new CompilationProblem(1500, "Unexecepted end of file", CompilationProblemType.Warning);
    assertNotEquals(warningAtLine1500WithMessageUnexceptedEndOfFile, errorAtLine10WithMessageHelloWorld);
  }

  @Test
  public void testGetMessage() throws Exception {
    assertThat(errorAtLine10WithMessageHelloWorld.getMessage(), is("Hello world"));
  }

  @Test
  public void testGetLine() throws Exception {
    assertThat(errorAtLine10WithMessageHelloWorld.getLine(), is(10L));
  }
}