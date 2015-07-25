package com.besaba.revonline.snippetide.api.datashare;

import java.util.function.Predicate;

public class StructureFieldInfo<T> {
  private final Class<T> type;
  private final Object defaultValue;
  private final String description;
  private final Predicate<T> validator;

  public StructureFieldInfo(final Class<T> type, final T defaultValue, final String description, final Predicate<T> validator) {
    this.type = type;
    this.defaultValue = defaultValue;
    this.description = description;
    this.validator = validator;
  }

  public Predicate<T> getValidator() {
    return validator;
  }

  public String getDescription() {
    return description;
  }

  public Class<T> getType() {
    return type;
  }

  public Object getDefaultValue() {
    return defaultValue;
  }
}
