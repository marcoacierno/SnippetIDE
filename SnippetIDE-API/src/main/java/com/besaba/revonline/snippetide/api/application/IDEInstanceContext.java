package com.besaba.revonline.snippetide.api.application;

import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.plugins.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class IDEInstanceContext {
  @NotNull
  private final Language language;
  @NotNull
  private final Plugin plugin;
  @Nullable
  private final Path originalFile;

  public IDEInstanceContext(@NotNull final Language language, @NotNull final Plugin plugin, @Nullable final Path originalFile) {
    this.language = language;
    this.plugin = plugin;
    this.originalFile = originalFile;
  }

  @NotNull
  public Language getLanguage() {
    return language;
  }

  @NotNull
  public Plugin getPlugin() {
    return plugin;
  }

  @Nullable
  public Path getOriginalFile() {
    return originalFile;
  }
}
