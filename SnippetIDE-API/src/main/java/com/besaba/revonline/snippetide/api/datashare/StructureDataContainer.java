package com.besaba.revonline.snippetide.api.datashare;

import com.besaba.revonline.snippetide.api.run.FieldInfo;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class StructureDataContainer {
  @NotNull
  private final ImmutableMap<String, FieldInfo> fields;
  @NotNull
  private final String name;
  private final int id;

  private StructureDataContainer(@NotNull final Map<String, FieldInfo> fields, final @NotNull String name, final int id) {
    this.name = name;
    this.id = id;
    this.fields = ImmutableMap.copyOf(fields);
  }

  public int getId() {
    return id;
  }

  @NotNull
  public ImmutableMap<String, FieldInfo> getFields() {
    return fields;
  }

  @NotNull
  public String getName() {
    return name;
  }

  public static class Builder {
    private final Map<String, FieldInfo> fields = new HashMap<>();
    private String name;
    private int id;

    public Builder(final int id) {
      this.id = id;

      if (this.id < 0) {
        throw new IllegalArgumentException("ID cannot be negative.");
      }
    }

    public Builder setId(final int id) {
      this.id = id;
      return this;
    }

    public Builder setName(final String name) {
      this.name = name;
      return this;
    }

    public Builder addField(final String name, final FieldInfo type) {
      this.fields.put(name, type);
      return this;
    }

    public StructureDataContainer create() {
      return new StructureDataContainer(fields, name, id);
    }
  }

}
