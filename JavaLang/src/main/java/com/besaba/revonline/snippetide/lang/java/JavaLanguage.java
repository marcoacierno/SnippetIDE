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
import com.besaba.revonline.snippetide.api.events.run.MessageFromProcess;
import com.besaba.revonline.snippetide.api.events.run.RunInformationEvent;
import com.besaba.revonline.snippetide.api.events.run.RunStartEvent;
import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.run.FieldInfo;
import com.besaba.revonline.snippetide.api.run.RunConfiguration;
import com.besaba.revonline.snippetide.api.run.RunConfigurationValues;
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
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

public class JavaLanguage implements Language {
  private static final int SIMPLE_RUN_CONFIGURATION_ID = 1;

  private final IDEApplication application = IDEApplicationLauncher.getIDEApplication();
  private final RunConfiguration runConfiguration = new RunConfiguration.Builder(SIMPLE_RUN_CONFIGURATION_ID)
      .setName("Run")
      .addField(
          "JRE Location",
          new FieldInfo(
              Path.class,
              Paths.get(System.getenv("JAVA_HOME")),
              "Location to your JRE/JDK"
          )
      )
      .create();
  private Optional<RunStartEvent> runningInformation = Optional.empty();

  @NotNull
  public String getName() {
    return "Java";
  }

  @NotNull
  public String[] getExtensions() {
    return new String[] {".java"};
  }

  @NotNull
  @Override
  public String getTemplate() {
    return "public class Solution {\n" +
        "\tpublic static void main(final String[] args) {\n" +
        "\t\tSystem.out.println(\"Hello world\");\n" +
        "\t}\n" +
        "}";
  }

  @NotNull
  @Override
  public RunConfiguration[] getRunConfigurations() {
    return new RunConfiguration[] {
        runConfiguration
    };
  }

  @Subscribe
  public void compileSnippetEvent(final CompileStartEvent event) {
    if (event.getTarget() != this) {
      return;
    }

    final Path sourceFile = event.getSourceFile();
    final Path outputDirectory = event.getOutputDirectory();

    final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    if (compiler == null) {
      final CompilationResult compilationResult = new CompilationResult(Collections.singletonList(
          new CompilationProblemBuilder()
              .setLine(0)
              .setMessage("Unable to create java compiler. Your JAVA_HOME should point to your JDK.")
              .setType(CompilationProblemType.Error)
              .createCompilationProblem()
      ));
      application.getEventManager().post(new CompileFinishedEvent(this, compilationResult));
      return;
    }

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
    if (runStartEvent.getTarget() != this) {
      return;
    }

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

    realRun(compileFinishedEvent);
  }

  private void realRun(final CompileFinishedEvent compileFinishedEvent) {
    final RunStartEvent runStartEvent = runningInformation.get();
    runningInformation = Optional.empty();

    if (!compileFinishedEvent.getCompilationResult().successfulCompilation()) {
      return;
    }

    final RunConfigurationValues runConfigurationValues = runStartEvent.getRunConfigurationValues();

    switch (runConfigurationValues.getParentId()) {
      case SIMPLE_RUN_CONFIGURATION_ID: {
        simpleRun(runStartEvent, runConfigurationValues);
        break;
      }
    }
  }

  private void simpleRun(final RunStartEvent runStartEvent,
                         final RunConfigurationValues runConfigurationValues) {
    final String javaHome = ((Path) runConfigurationValues.getValues().get("JRE Location")).toAbsolutePath().toString();

    final Path classFile = Paths.get(
        Files.getNameWithoutExtension(runStartEvent.getSourceFile().getFileName().toString())
    );

    if (javaHome == null) {
      application.getEventManager().post(new MessageFromProcess("Unable to run, missing JAVA_HOME variable."));
      return;
    }

    final String command = "\"" + javaHome + File.separator + "bin"  + File.separator + "java\" " + classFile + " -cp \"" + runStartEvent.getSourceFile().getParent() + "\"";
    application.getEventManager().post(new RunInformationEvent(command, runStartEvent));
  }
}
