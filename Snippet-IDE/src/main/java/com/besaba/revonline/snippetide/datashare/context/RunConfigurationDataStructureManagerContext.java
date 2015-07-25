package com.besaba.revonline.snippetide.datashare.context;

import com.besaba.revonline.snippetide.api.datashare.StructureDataContainer;
import com.besaba.revonline.snippetide.api.language.Language;
import com.besaba.revonline.snippetide.api.plugins.Plugin;
import com.besaba.revonline.snippetide.configuration.contract.ConfigurationSettingsContract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class RunConfigurationDataStructureManagerContext extends DataStructureManagerContext {
  @NotNull
  private final Plugin plugin;
  @NotNull
  private final Language language;

  public RunConfigurationDataStructureManagerContext(@NotNull final Plugin plugin,
                                                     @NotNull final Language language) {
    this.plugin = plugin;
    this.language = language;
  }

  @Override
  public String getDataContainerConfigurationNode(final int data) {
    return ConfigurationSettingsContract.RunConfigurations.generateRunConfigurationsLanguageQuery(plugin, language)
        + "." + data;
  }

  @Override
  public String getDefaultDataContainerConfigurationNode() {
    return ConfigurationSettingsContract.RunConfigurations.generateLanguageDefaultRunConfigurationQuery(plugin, language);
  }

  @Override
  public StructureDataContainer getStructureFromId(final int structureId) {
    return Arrays.stream(language.getRunConfigurations())
        .filter(structure -> structure.getId() == structureId)
        .findFirst()
        .orElseThrow(AssertionError::new);
  }

  @Override
  public StructureDataContainer[] getDataContainerStructures() {
    return language.getRunConfigurations();
  }
}
