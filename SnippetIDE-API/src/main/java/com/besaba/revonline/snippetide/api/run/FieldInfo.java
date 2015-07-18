package com.besaba.revonline.snippetide.api.run;

public class FieldInfo {
  private final Class<?> type;
  private final Object defaultValue;
  private final String description;

  public FieldInfo(final Class<?> type, final Object defaultValue, final String description) {
    this.type = type;
    this.defaultValue = defaultValue;
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public Class<?> getType() {
    return type;
  }

  public Object getDefaultValue() {
    return defaultValue;
  }
}
