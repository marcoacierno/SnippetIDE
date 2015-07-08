package com.besaba.revonline.snippetide.lang.java;

import com.besaba.revonline.snippetide.api.events.compile.CompileStartEvent;
import com.besaba.revonline.snippetide.api.events.Event;
import com.besaba.revonline.snippetide.api.events.EventKind;
import com.besaba.revonline.snippetide.api.language.Language;
import org.jetbrains.annotations.NotNull;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

public class JavaLanguage implements Language {
  @NotNull
  public String getName() {
    return "Java";
  }

  @NotNull
  public String[] getExtensions() {
    return new String[] {"java"};
  }

  @NotNull
  public Set<EventKind> listenTo() {
    return EnumSet.of(EventKind.COMPILE_START);
  }

  public boolean receiveEvent(Event<?> event) {
    // here I'll support only compile and execution
    // so I can execute the check here
    if (event.getTarget() != this) {
      return false;
    }

    switch (event.getType()) {
      case COMPILE_START: {
        final CompileStartEvent compileStartEvent = (CompileStartEvent) event;
        compile(compileStartEvent);
        break;
      }
    }

    return true;
  }

  private void compile(final CompileStartEvent event) {
    final Path sourceFile = event.getSourceFile();
    final Path outputDirectory = event.getOutputDirectory();

    final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    final DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();
    final StandardJavaFileManager fileManager = compiler.getStandardFileManager(
        diagnosticCollector,
        Locale.ENGLISH,
        StandardCharsets.UTF_8
    );

    final Iterable<? extends JavaFileObject> sourceUnit = fileManager.getJavaFileObjectsFromFiles(
        Collections.singletonList(sourceFile.toFile())
    );

    final Iterable<String> options = Arrays.asList("-d", outputDirectory.toAbsolutePath().toString());

    compiler.getTask(null, fileManager, diagnosticCollector, options, null, sourceUnit).call();


  }
}
