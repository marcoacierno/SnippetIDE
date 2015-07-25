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
import com.besaba.revonline.snippetide.api.datashare.StructureDataContainer;
import com.besaba.revonline.snippetide.api.datashare.DataContainer;
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
  private final StructureDataContainer structureDataContainer = new StructureDataContainer.Builder(SIMPLE_RUN_CONFIGURATION_ID)
      .setName("Run")
      .addField(
          "JRE Location",
          new FieldInfo<>(
              Path.class,
              System.getenv("JAVA_HOME") != null ? Paths.get(System.getenv("JAVA_HOME")) : Paths.get("."),
              "Location to your JRE/JDK",
              path -> path != null && !java.nio.file.Files.isDirectory(path)
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
    return "import java.io.BufferedReader;\n" +
        "import java.io.IOException;\n" +
        "import java.io.InputStreamReader;\n" +
        "\n" +
        "public class Solution {\n" +
        "  public static void main(final String[] args) throws IOException {\n" +
        "    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));\n" +
        "    System.out.println(\"Write your message!\");\n" +
        "    final String message = bufferedReader.readLine();\n" +
        "    System.out.println(message);\n" +
        "  }\n" +
        "}";
  }

  @NotNull
  @Override
  public StructureDataContainer[] getRunConfigurations() {
    return new StructureDataContainer[] {
        structureDataContainer
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

    final DataContainer dataContainer = runStartEvent.getDataContainer();

    switch (dataContainer.getParentId()) {
      case SIMPLE_RUN_CONFIGURATION_ID: {
        simpleRun(runStartEvent, dataContainer);
        break;
      }
    }
  }

  private void simpleRun(final RunStartEvent runStartEvent,
                         final DataContainer dataContainer) {
    final String javaHome = ((Path) dataContainer.getValues().get("JRE Location")).toAbsolutePath().toString();

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
