package com.besaba.revonline.snippetide.api.run;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class RunConfigurationValues {
  @NotNull
  private final RunConfiguration parent;
  @NotNull
  private final ImmutableMap<String, Object> values;

  public RunConfigurationValues(@NotNull final RunConfiguration parent,
                                @NotNull final Map<String, Object> values) {
    this.parent = parent;
    this.values = ImmutableMap.copyOf(values);
  }

  @NotNull
  public RunConfiguration getParent() {
    return parent;
  }

  @NotNull
  public ImmutableMap<String, Object> getValues() {
    return values;
  }
}
