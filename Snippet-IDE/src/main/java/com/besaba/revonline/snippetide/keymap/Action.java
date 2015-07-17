package com.besaba.revonline.snippetide.keymap;

import com.besaba.revonline.snippetide.configuration.contract.ConfigurationSettingsContract;
import org.jetbrains.annotations.NotNull;

public enum Action {
  Compile(ConfigurationSettingsContract.Keymap.COMPILE_ENTRY),
  Run(ConfigurationSettingsContract.Keymap.RUN_ENTRY);

  @NotNull
  private final String entry;

  Action(@NotNull final String entry) {
    this.entry = entry;
  }

  @NotNull
  public String getSettingsEntry() {
    return entry;
  }
}
