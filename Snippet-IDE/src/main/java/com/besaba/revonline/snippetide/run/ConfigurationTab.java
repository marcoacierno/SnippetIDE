package com.besaba.revonline.snippetide.run;

import com.besaba.revonline.snippetide.api.datashare.StructureDataContainer;
import javafx.scene.Node;
import javafx.scene.control.Tab;

public class ConfigurationTab extends Tab {
  private final StructureDataContainer structureDataContainer;

  public ConfigurationTab(final StructureDataContainer structureDataContainer) {
    this.structureDataContainer = structureDataContainer;
  }

  public ConfigurationTab(final String text, final StructureDataContainer structureDataContainer) {
    super(text);
    this.structureDataContainer = structureDataContainer;
  }

  public ConfigurationTab(final String text, final Node content, final StructureDataContainer structureDataContainer) {
    super(text, content);
    this.structureDataContainer = structureDataContainer;
  }

  public StructureDataContainer getStructureDataContainer() {
    return structureDataContainer;
  }
}
