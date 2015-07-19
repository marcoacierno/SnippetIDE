package com.besaba.revonline.snippetide.run;

import com.besaba.revonline.snippetide.api.application.IDEApplicationLauncher;
import com.besaba.revonline.snippetide.api.configuration.Configuration;
import com.besaba.revonline.snippetide.api.run.RunConfigurationValues;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;

class RunConfigurationValuesManagerData {
  @NotNull
  private final RunConfigurationValues runConfigurationValues;
  @NotNull
  private final String configurationName;
  private boolean defaultConfiguration;

  public RunConfigurationValuesManagerData(@NotNull final RunConfigurationValues runConfigurationValues,
                                           @NotNull final String configurationName,
                                           final boolean defaultConfiguration) {
    this.runConfigurationValues = runConfigurationValues;
    this.configurationName = configurationName;
    this.defaultConfiguration = defaultConfiguration;
  }

  @NotNull
  public RunConfigurationValues getRunConfigurationValues() {
    return runConfigurationValues;
  }

  @NotNull
  public String getConfigurationName() {
    return configurationName;
  }

  public boolean isDefault() {
    return defaultConfiguration;
  }

  public void setDefaultConfiguration(final boolean defaultConfiguration) {
    this.defaultConfiguration = defaultConfiguration;
  }
}
