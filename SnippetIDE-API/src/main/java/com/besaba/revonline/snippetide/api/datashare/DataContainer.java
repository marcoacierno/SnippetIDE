package com.besaba.revonline.snippetide.api.datashare;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DataContainer {
  private final int parent;
  @NotNull
  private final ImmutableMap<String, Object> values;

  public DataContainer(@NotNull final StructureDataContainer parent,
                       @NotNull final Map<String, Object> values) {
    this.parent = parent.getId();
    this.values = ImmutableMap.copyOf(values);
  }

  public DataContainer(final int parent,
                       @NotNull final Map<String, Object> values) {
    this.parent = parent;
    this.values = ImmutableMap.copyOf(values);
  }

  @NotNull
  public ImmutableMap<String, Object> getValues() {
    return values;
  }

  public int getParentId() {
    return parent;
  }
}
