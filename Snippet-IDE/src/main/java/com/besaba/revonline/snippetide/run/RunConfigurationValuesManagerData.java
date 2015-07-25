package com.besaba.revonline.snippetide.run;

import com.besaba.revonline.snippetide.api.datashare.DataContainer;
import org.jetbrains.annotations.NotNull;

class RunConfigurationValuesManagerData {
  @NotNull
  private final DataContainer dataContainer;
  @NotNull
  private final String configurationName;
  private boolean defaultConfiguration;

  public RunConfigurationValuesManagerData(@NotNull final DataContainer dataContainer,
                                           @NotNull final String configurationName,
                                           final boolean defaultConfiguration) {
    this.dataContainer = dataContainer;
    this.configurationName = configurationName;
    this.defaultConfiguration = defaultConfiguration;
  }

  @NotNull
  public DataContainer getDataContainer() {
    return dataContainer;
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
