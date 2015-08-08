package com.besaba.revonline.snippetide.lang.markdown;

import com.besaba.revonline.snippetide.api.application.IDEApplicationLauncher;
import com.besaba.revonline.snippetide.api.datashare.StructureDataContainer;
import com.besaba.revonline.snippetide.api.events.manager.EventManager;
import com.besaba.revonline.snippetide.api.events.run.MessageFromProcess;
import com.besaba.revonline.snippetide.api.events.run.RunInformationEvent;
import com.besaba.revonline.snippetide.api.events.run.RunStartEvent;
import com.besaba.revonline.snippetide.api.language.Language;
import com.github.rjeschke.txtmark.Processor;
import com.google.common.eventbus.Subscribe;

import java.io.IOException;
import java.nio.file.Files;

public class MarkdownLanguage implements Language {
  public String getName() {
    return "Markdown";
  }

  public String[] getExtensions() {
    return new String[] {".md"};
  }

  public String getTemplate() {
    return "**Hello** __world__!";
  }

  public StructureDataContainer[] getRunConfigurations() {
    return new StructureDataContainer[0];
  }

  @Subscribe
  public void runPressed(final RunStartEvent runStartEvent) {
    if (runStartEvent.getTarget() != this) {
      return;
    }

    final EventManager eventManager = IDEApplicationLauncher.getIDEApplication().getEventManager();
    final String textFromFile;

    try {
      textFromFile = Files
          .readAllLines(runStartEvent.getSourceFile())
          .stream()
          .reduce("", (acc, nxt) -> acc + "\n" + nxt);
    } catch (IOException e) {
      eventManager.post(new MessageFromProcess("Unable to process the text :("));
      return;
    }

    final String markdownText = Processor.process(textFromFile);

    eventManager.post(RunInformationEvent.noExternalProcess());
    eventManager.post(new MessageFromProcess(markdownText));
  }
}
