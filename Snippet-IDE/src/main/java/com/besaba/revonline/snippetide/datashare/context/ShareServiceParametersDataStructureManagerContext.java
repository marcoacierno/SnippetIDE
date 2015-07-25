package com.besaba.revonline.snippetide.datashare.context;

import com.besaba.revonline.snippetide.api.datashare.StructureDataContainer;
import com.besaba.revonline.snippetide.api.plugins.Plugin;
import com.besaba.revonline.snippetide.api.shareservices.ShareService;
import com.besaba.revonline.snippetide.configuration.contract.ConfigurationSettingsContract.ShareOnConfigurations;

import java.util.Arrays;

public class ShareServiceParametersDataStructureManagerContext extends DataStructureManagerContext {
  private final Plugin plugin;
  private final ShareService shareService;

  public ShareServiceParametersDataStructureManagerContext(final Plugin plugin, final ShareService shareService) {
    this.plugin = plugin;
    this.shareService = shareService;
  }

  @Override
  public String getDataContainerConfigurationNode(final int data) {
    return ShareOnConfigurations.generateShareOnServiceQuery(plugin, shareService) + "." + data;
  }

  @Override
  public String getDefaultDataContainerConfigurationNode() {
    return ShareOnConfigurations.generateLanguageDefaultRunConfigurationQuery(plugin, shareService);
  }

  @Override
  public StructureDataContainer getStructureFromId(final int structureId) {
    return Arrays.stream(shareService.getShareParameters())
        .filter(structure -> structure.getId() == structureId)
        .findFirst()
        .orElseThrow(AssertionError::new);
  }

  @Override
  public StructureDataContainer[] getDataContainerStructures() {
    return shareService.getShareParameters();
  }
}
