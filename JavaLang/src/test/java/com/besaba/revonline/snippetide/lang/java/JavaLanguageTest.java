package com.besaba.revonline.snippetide.lang.java;

import com.besaba.revonline.snippetide.api.application.IDEApplication;
import com.besaba.revonline.snippetide.api.application.IDEApplicationLauncher;
import com.besaba.revonline.snippetide.api.events.compile.CompileStartEvent;
import com.besaba.revonline.snippetide.api.events.compile.CompileStartEventBuilder;
import com.besaba.revonline.snippetide.api.events.manager.EventManager;
import com.besaba.revonline.snippetide.api.plugins.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class JavaLanguageTest {
  private JavaLanguage language;
  private Path javaSourceWithOneWarningAndZeroErrors;
  private Path outputDirectory;

  // <editor-fold name="Application">
  static {
    IDEApplicationLauncher.createApplication(new IDEApplication() {
      @NotNull
      @Override
      public EventManager getEventManager() {
        return null;
      }

      @NotNull
      @Override
      public PluginManager getPluginManager() {
        return null;
      }

      @NotNull
      @Override
      public Path getApplicationDirectory() {
        return null;
      }

      @NotNull
      @Override
      public Path getPluginsDirectory() {
        return null;
      }

      @NotNull
      @Override
      public Path getTemporaryDirectory() {
        return null;
      }
    });
  }
  // </editor-fold>

  @Before
  public void setUp() throws Exception {
    language = new JavaLanguage();

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