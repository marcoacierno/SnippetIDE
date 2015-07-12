package com.besaba.revonline.snippetide.lang.java;

import com.besaba.revonline.snippetide.api.application.IDEApplication;
import com.besaba.revonline.snippetide.api.application.IDEApplicationLauncher;
import com.besaba.revonline.snippetide.api.compiler.CompilationProblem;
import com.besaba.revonline.snippetide.api.compiler.CompilationProblemBuilder;
import com.besaba.revonline.snippetide.api.compiler.CompilationProblemType;
import com.besaba.revonline.snippetide.api.compiler.CompilationResult;
import com.besaba.revonline.snippetide.api.events.compile.CompileFinishedEvent;
import com.besaba.revonline.snippetide.api.events.compile.CompileStartEvent;
import com.besaba.revonline.snippetide.api.events.compile.CompileStartEventBuilder;
import com.besaba.revonline.snippetide.api.events.run.RunInformationEvent;
import com.besaba.revonline.snippetide.api.events.run.RunStartEvent;
import com.besaba.revonline.snippetide.api.language.Language;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.Files;
import org.jetbrains.annotations.NotNull;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

public class JavaLanguage implements Language {
  private final IDEApplication application = IDEApplicationLauncher.getIDEApplication();
  private Optional<RunStartEvent> runningInformation = Optional.empty();

  @NotNull
  public String getName() {
    return "Java";
  }

  @NotNull
  public String[] getExtensions() {
    return new String[] {".java"};
  }

  @Subscribe
  public void compileSnippetEvent(final CompileStartEvent event) {
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

    final ImmutableList.Builder<CompilationProblem> listBuilder = ImmutableList.builder();

    diagnosticCollector.getDiagnostics().forEach(diagnostic -> listBuilder.add(new CompilationProblemBuilder()
        .setMessage(diagnostic.getMessage(Locale.ENGLISH))
        .setLine(diagnostic.getLineNumber())
        .setType(diagnostic.getKind() == Diagnostic.Kind.ERROR ? CompilationProblemType.Error : CompilationProblemType.Warning)
        .createCompilationProblem()
    ));

    final CompilationResult compilationResult = new CompilationResult(listBuilder.build());

    application.getEventManager().post(new CompileFinishedEvent(this, compilationResult));
  }

  @Subscribe
  public void runSnippetEvent(final RunStartEvent runStartEvent) {
    final CompileStartEvent compileStartEvent = new CompileStartEventBuilder()
        .setTarget(this)
        .setSourceFile(runStartEvent.getSourceFile())
        .setOutputDirectory(runStartEvent.getTemporaryDirectory())
        .build();

    runningInformation = Optional.of(runStartEvent);
    compileSnippetEvent(compileStartEvent);
  }

  @Subscribe
  public void compileEndedEvent(final CompileFinishedEvent compileFinishedEvent) {
    if (!runningInformation.isPresent()) {
      return;
    }

    final RunStartEvent runStartEvent = runningInformation.get();

    if (!compileFinishedEvent.getCompilationResult().successfulCompilation()) {
      return;
    }

    final Path classFile = Paths.get(
        Files.getNameWithoutExtension(runStartEvent.getSourceFile().getFileName().toString())
    );

    final String javaHome = System.getenv("JAVA_HOME");

    if (javaHome == null) {
      return;
    }

    final String command = "\"" + javaHome + "\\bin\\java\" " + classFile + " -cp " + runStartEvent.getSourceFile().getParent();
//    final String command = "\"" + "D:\\Program Files\\Java\\jdk1.8.0_45" + "\\bin\\java\" " + classFile + " -cp \"" + runStartEvent.getSourceFile().getParent() + "\"";
    application.getEventManager().post(new RunInformationEvent(command, runStartEvent));

    runningInformation = Optional.empty();
  }
}
