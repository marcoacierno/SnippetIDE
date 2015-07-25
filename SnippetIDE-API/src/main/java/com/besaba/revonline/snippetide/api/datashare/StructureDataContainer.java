package com.besaba.revonline.snippetide.api.datashare;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class StructureDataContainer {
  @NotNull
  private final ImmutableMap<String, StructureFieldInfo> fields;
  @NotNull
  private final String name;
  private final int id;

  private StructureDataContainer(@NotNull final Map<String, StructureFieldInfo> fields, final @NotNull String name, final int id) {
    this.name = name;
    this.id = id;
    this.fields = ImmutableMap.copyOf(fields);
  }

  public int getId() {
    return id;
  }

  @NotNull
  public ImmutableMap<String, StructureFieldInfo> getFields() {
    return fields;
  }

  @NotNull
  public String getName() {
    return name;
  }

  public static class Builder {
    private final Map<String, StructureFieldInfo> fields = new HashMap<>();
    private String name;
    private final int id;

    public Builder(final int id) {
      this.id = id;

      if (this.id < 0) {
        throw new IllegalArgumentException("ID cannot be negative.");
      }
    }

    public Builder setName(final String name) {
      this.name = name;
      return this;
    }

    public Builder addField(final String name, final StructureFieldInfo type) {
      this.fields.put(name, type);
      return this;
    }

    public StructureDataContainer create() {
      return new StructureDataContainer(fields, name, id);
    }
  }

}
