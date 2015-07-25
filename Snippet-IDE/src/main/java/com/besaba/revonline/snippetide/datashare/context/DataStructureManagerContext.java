package com.besaba.revonline.snippetide.datashare.context;

import com.besaba.revonline.snippetide.api.datashare.StructureDataContainer;

public abstract class DataStructureManagerContext {
  public abstract String getDataContainerConfigurationNode(final int data);
  public abstract String getDefaultDataContainerConfigurationNode();
  public abstract StructureDataContainer getStructureFromId(final int structureId);
  public abstract StructureDataContainer[] getDataContainerStructures();
}
