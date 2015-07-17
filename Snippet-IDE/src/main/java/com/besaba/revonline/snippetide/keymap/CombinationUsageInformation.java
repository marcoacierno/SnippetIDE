package com.besaba.revonline.snippetide.keymap;

import javafx.scene.input.KeyCodeCombination;
import org.jetbrains.annotations.NotNull;

public class CombinationUsageInformation {
  @NotNull
  private final Action action;
  @NotNull
  private final KeyCodeCombination codeCombination;

  public CombinationUsageInformation(@NotNull final Action action,
                                     @NotNull final KeyCodeCombination codeCombination) {
    this.action = action;
    this.codeCombination = codeCombination;
  }

  @NotNull
  public Action getAction() {
    return action;
  }

  @NotNull
  public KeyCodeCombination getCodeCombination() {
    return codeCombination;
  }
}
