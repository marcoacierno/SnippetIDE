package com.besaba.revonline.snippetide.lang.java;

import com.besaba.revonline.snippetide.api.events.compile.CompileStartEvent;
import com.besaba.revonline.snippetide.api.events.compile.CompileStartEventBuilder;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class JavaLanguageTest {
  private JavaLanguage language = new JavaLanguage();
  private Path javaSourceWithOneWarningAndZeroErrors;
  private Path outputDirectory;

  @Before
  public void setUp() throws Exception {
    javaSourceWithOneWarningAndZeroErrors = Paths.get(
        JavaLanguageTest.class.getResource("java_sourcewithonewarningandzeroerrors.java").toURI()
    );
    outputDirectory = Paths.get(
        JavaLanguageTest.class.getResource(".").toURI()
    );
  }

  @Test
  public void testGetName() throws Exception {
    assertEquals("Java", language.getName());
  }

  @Test
  public void testGetExtensions() throws Exception {
    assertArrayEquals(new String[] {"java"}, language.getExtensions());
  }

  public void testCompilation() throws Exception {
    final CompileStartEvent compileStartEvent = new CompileStartEventBuilder()
        .setTarget(language)
        .setSourceFile(javaSourceWithOneWarningAndZeroErrors)
        .setOutputDirectory(outputDirectory)
        .build();

    language.compileSnippetEvent(compileStartEvent);
  }
}