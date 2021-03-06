package com.besaba.revonline.snippetide.api.events.run;

import com.besaba.revonline.snippetide.api.events.Event;
import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.datashare.DataContainer;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * Event sent when the user wants to run a snippet.
 *
 * It contains the location to the source and
 * a temporary directory if needed to save
 * temporary files.
 *
 * Plugin cannot run a snippet directly,
 * they need to send the command (and
 * other information) inside the
 * response event RunInformationEvent
 * so that the IDE can create a process
 * and execute the command passed.
 *
 *
 * @see RunInformationEvent
 */
public class RunStartEvent extends Event<Language> {
  @NotNull
  private final Path sourceFile;
  @NotNull
  private final Path temporaryDirectory;
  @NotNull
  private final DataContainer dataContainer;

  public RunStartEvent(@NotNull final Language target,
                       @NotNull final Path sourceFile,
                       @NotNull final Path temporaryDirectory,
                       @NotNull final DataContainer dataContainer) {
    super(target);
    this.sourceFile = sourceFile;
    this.temporaryDirectory = temporaryDirectory;
    this.dataContainer = dataContainer;
  }

  @NotNull
  public Path getSourceFile() {
    return sourceFile;
  }

  @NotNull
  public Path getTemporaryDirectory() {
    return temporaryDirectory;
  }

  @NotNull
  public DataContainer getDataContainer() {
    return dataContainer;
  }
}
