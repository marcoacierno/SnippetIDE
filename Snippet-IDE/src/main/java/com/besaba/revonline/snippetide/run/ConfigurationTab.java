package com.besaba.revonline.snippetide.run;

import com.besaba.revonline.snippetide.api.run.RunConfiguration;
import javafx.scene.Node;
import javafx.scene.control.Tab;

public class ConfigurationTab extends Tab {
  private final RunConfiguration runConfiguration;

  public ConfigurationTab(final RunConfiguration runConfiguration) {
    this.runConfiguration = runConfiguration;
  }

  public ConfigurationTab(final String text, final RunConfiguration runConfiguration) {
    super(text);
    this.runConfiguration = runConfiguration;
  }

  public ConfigurationTab(final String text, final Node content, final RunConfiguration runConfiguration) {
    super(text, content);
    this.runConfiguration = runConfiguration;
  }

  public RunConfiguration getRunConfiguration() {
    return runConfiguration;
  }
}
